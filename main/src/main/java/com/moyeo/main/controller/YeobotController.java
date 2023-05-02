package com.moyeo.main.controller;


import com.moyeo.main.dto.TravelRecommendRequest;
import com.moyeo.main.entity.User;
import com.moyeo.main.repository.UserRepository;
import com.moyeo.main.service.YeobotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/auth/yeobot")
@RestController
@Slf4j
public class YeobotController {

    private final YeobotService yeobotService;
    private final UserRepository userRepository;

    /*
    *
    * 로그인 기능 정상작동 이후에 로그인 로직으로 변경 예정
    * -> @requestparam userid 삭제 및 주석처리된 로그인 정보에서 uid받아오는 로직으로 변경
    *
    * */

    //유저가 여행중인지 여부 반환
    @PostMapping("/istravelling")
    public ResponseEntity<String> getLatestTimelineStatus(@RequestParam("userId") Long userId) throws Exception{
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception());
        //로그인 정보에서 uid 받아오기
//        Long userId = null;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth != null && auth.getPrincipal() != null) {
//            User user = (User) auth.getPrincipal();
//            userId = user.getUserId();
//        }

        //여행중 여부 반환
        String status = yeobotService.getLatestTimelineStatus(user.getUserId());
        System.out.println("여긴가3");
        return ResponseEntity.ok(status);
    }

    //여행중인 유저 맛집 추천
    @PostMapping("/ing/dining")
    public ResponseEntity<String> restaurantRecommendations(@RequestParam Long userId) throws Exception{
        //로그인 정보에서 uid 받아오기
//        Long userId = null;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth != null && auth.getPrincipal() != null) {
//            User user = (User) auth.getPrincipal();
//            userId = user.getUserId();
//        }
        // 최신 주소 반환
        List<String[]> latestAddress = yeobotService.findLatestAddress(userId);

        // 프롬프트 반환
//        String[] trimmedAddress = Arrays.stream(latestAddress)
//                .map(String::trim)
//                .toArray(String[]::new);
//
//        String joinedAddress = String.join(" ", trimmedAddress);
//        String goal = "Search for a good restaurant near " + joinedAddress.replaceAll(",", " ") + ".";
        List<String> addressList = new ArrayList<>();

        for (String[] addresses : latestAddress) {
            for (String address : addresses) {
                addressList.add(String.valueOf(address));
            }
        }

        String goal = "Search for a good restaurant near " + addressList.get(0) +" "+ addressList.get(1) +" "+ addressList.get(2) +" "+ addressList.get(3) +".";
        return ResponseEntity.ok(goal);
    }

    //여행중인 유저 액티비티 추천
    @PostMapping("/ing/activity")
    public ResponseEntity<String> activityRecommendationsForTraveller(@RequestParam Long userId) throws Exception{
        //로그인 정보에서 uid 받아오기
//        Long userId = null;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth != null && auth.getPrincipal() != null) {
//            User user = (User) auth.getPrincipal();
//            userId = user.getUserId();
//        }
        // 최신 주소 반환
        List<String[]> latestAddress = yeobotService.findLatestAddress(userId);

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

        return ResponseEntity.ok(goal);
    }

    //여행중이 아닌 유저 여행지 추천
    @PostMapping("/yet/place")
    public ResponseEntity<String> recommendPlace(@RequestBody TravelRecommendRequest request) {
        // request에서 필요한 정보를 추출해서 변수에 저장
        String destination = request.getDestination();
        String season = request.getSeason();
        String purpose = request.getPurpose();

        //
        String goal = "Recommend me a good place for travel to go in " + destination + " in " + season + " for " + purpose +".";

        // 프롬프트 반환
        return ResponseEntity.ok(goal);
    }


    //여행중이 아닌 유저 액티비티 추천
    @PostMapping("/yet/activity")
    public ResponseEntity<String> recommendActivities(@RequestBody TravelRecommendRequest request) {
        // request에서 필요한 정보를 추출해서 변수에 저장
        String destination = request.getDestination();
        String season = request.getSeason();

        // 추천 결과 문자열 생성
        String goal = "Recommend me some fun things to do near " + destination + " during " + season +".";

        // 프롬프트 반환
        return ResponseEntity.ok(goal);

    }

}
