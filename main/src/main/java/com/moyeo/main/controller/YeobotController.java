package com.moyeo.main.controller;


import com.moyeo.main.component.YeobotClient;
import com.moyeo.main.dto.TravelRecommendRequest;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.service.ChatService;
import com.moyeo.main.service.FcmService;
import com.moyeo.main.service.MessageBoxService;
import com.moyeo.main.service.YeobotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/auth/yeobot")
@RestController
@Log4j2
public class YeobotController {

    private final YeobotService yeobotService;
    private final YeobotClient yeobotClient;
    private final ChatService chatService;
    private final FcmService fcmService;
    private final MessageBoxService messageBoxService;

    //유저가 여행중인지 여부 반환
    @PostMapping("/istravelling")
    public ResponseEntity<String> getLatestTimelineStatus(){
        log.info("유저 여행중 여부 파악 로직 시작");
        //로그인 정보에서 uid 받아오기
        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            userId = user.getUserId();
        }

        //여행중 여부 반환
        String status = yeobotService.getLatestTimelineStatus(userId);

        log.info("유저 여행중 여부 파악 로직 완료");
        return ResponseEntity.ok(status);
    }


    //여행중인 유저 맛집 추천
    @PostMapping("/ing/dining")
    public ResponseEntity<String> restaurantRecommendations() {
        log.info("맛집추천 spring 내부 로직 시작");
        //로그인 정보에서 uid 받아오기
        Long userId = null;
        User user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            user = (User) auth.getPrincipal();
            userId = user.getUserId();
        }
        // 최신 주소 반환
        List<String[]> latestAddress = null;
        try {
            latestAddress = yeobotService.findLatestAddress(userId);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<String> addressList = new ArrayList<>();

        for (String[] addresses : latestAddress) {
            for (String address : addresses) {
                addressList.add(String.valueOf(address));
            }
        }
    
        //프롬프트 반환
        String goal = "Search for a good restaurant near " + addressList.get(0) +" "+ addressList.get(1) +" "+ addressList.get(2) +" "+ addressList.get(3) +".";
        String caseType = "restaurant";

        //goal 제대로 만들어졌는지 점검
        System.out.println(goal);
        log.info("goal : {}",goal);

        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData(caseType, goal);

        log.info("response insert 작업 시작");
        // MongoDB에 저장
        chatService.insertResponse(user, result);
        log.info("mongoDB에 저장 완료");
        // MessageBox에 저장
        messageBoxService.insertMessage(user.getUserId(), result);
        log.info("messageBox에 저장 완료");
        log.info("response insert 작업 완료");

        fcmService.send(user);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    //여행중인 유저 액티비티 추천
    @PostMapping("/ing/activity")
    public ResponseEntity<String> activityRecommendationsForTraveller() {
        log.info("여행중인 유저에게 액티비티추천 spring 내부 로직 시작");
        //로그인 정보에서 uid 받아오기
        Long userId = null;
        User user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            user = (User) auth.getPrincipal();
            userId = user.getUserId();
        }
        // 최신 주소 반환
        List<String[]> latestAddress = null;
        try {
            latestAddress = yeobotService.findLatestAddress(userId);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<String> addressList = new ArrayList<>();

        for (String[] addresses : latestAddress) {
            for (String address : addresses) {
                addressList.add(String.valueOf(address));
            }
        }

        // 프롬프트 반환
        String goal = "Recommend me some fun things to do near "+ addressList.get(0) + " "
                + addressList.get(1) + " "
                + addressList.get(2) + " today.";

        String caseType = "activity";

        //goal 제대로 만들어졌는지 점검
        System.out.println(goal);
        log.info("goal : {}",goal);

        log.info("여행중인 유저에게 액티비티추천 spring 내부 로직 완료");

        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData(caseType, goal);

        log.info("response insert 작업 시작");
        // MongoDB에 저장
        chatService.insertResponse(user, result);
        log.info("mongoDB에 저장 완료");
        // MessageBox에 저장
        messageBoxService.insertMessage(user.getUserId(), result);
        log.info("messageBox에 저장 완료");
        log.info("response insert 작업 완료");

        fcmService.send(user);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    //여행중이 아닌 유저 여행지 추천
    @PostMapping("/yet/place")
    public ResponseEntity<String> recommendPlace(@RequestBody TravelRecommendRequest request) {
        log.info("여행지추천 spring 내부 로직 시작");

        // request에서 필요한 정보를 추출해서 변수에 저장
        String destination = request.getDestination();
        String season = request.getSeason();
        String purpose = request.getPurpose();

        String goal = "Search for a good place for travel to go in " + destination + " in " + season + " for " + purpose +".";

        String caseType = "place";

        //goal 제대로 만들어졌는지 점검
        System.out.println(goal);
        log.info("goal : {}",goal);
        log.info("goal : {}",goal.toString());

        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData(caseType, goal);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        System.out.println(user + " is user");

        log.info("response insert 작업 시작");
        // MongoDB에 저장
        chatService.insertResponse(user, result);
        log.info("mongoDB에 저장 완료");
        // MessageBox에 저장
        messageBoxService.insertMessage(user.getUserId(), result);
        log.info("messageBox에 저장 완료");
        log.info("response insert 작업 완료");

        fcmService.send(user);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    //여행중이 아닌 유저 액티비티 추천
    @PostMapping("/yet/activity")
    public ResponseEntity<String> recommendActivities(@RequestBody TravelRecommendRequest request) {
        log.info("여행중이 아닌 유저에게 액티비티 추천 spring 내부 로직 시작");

        // request에서 필요한 정보를 추출해서 변수에 저장
        String destination = request.getDestination();
        String season = request.getSeason();

        // 추천 결과 문자열 생성
        String goal = "Search for things to do when traveling in or around " + destination + " during " + season +".";
        String caseType = "activity";

        //goal 제대로 만들어졌는지 점검
        System.out.println(goal);
        log.info("goal : {}",goal);

        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData(caseType, goal);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        System.out.println(user + " is user");

        log.info("response insert 작업 시작");
        // MongoDB에 저장
        chatService.insertResponse(user, result);
        log.info("mongoDB에 저장 완료");
        // MessageBox에 저장
        messageBoxService.insertMessage(user.getUserId(), result);
        log.info("messageBox에 저장 완료");
        log.info("response insert 작업 완료");

        fcmService.send(user);

        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}
