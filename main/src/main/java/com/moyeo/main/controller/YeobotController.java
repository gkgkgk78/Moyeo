package com.moyeo.main.controller;


import com.moyeo.main.dto.TravelRecommendRequest;
import com.moyeo.main.entity.User;
import com.moyeo.main.service.YeobotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth/yeobot")
@RestController
@Slf4j
public class YeobotController {

    private final YeobotService yeobotService;

    //유저가 여행중인지 여부 반환
    @PostMapping("/istravelling")
    public ResponseEntity<String> getLatestTimelineStatus() throws Exception{

        //로그인 정보에서 uid 받아오기
        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            userId = user.getUserId();
        }

        //여행중 여부 반환
        String status = yeobotService.getLatestTimelineStatus(userId);
        return ResponseEntity.ok(status);
    }

    //여행중인 유저 맛집 추천
    @PostMapping("/ing/dining")
    public ResponseEntity<String> restaurantRecommendations() throws Exception{
        //로그인 정보에서 uid 받아오기
        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            userId = user.getUserId();
        }
        // 최신 주소 반환
        String[] latestAddress = yeobotService.findLatestAddress(userId);

        // 프롬프트 반환
        String goal = "Search for a good restaurant near " + latestAddress[0] + " " + latestAddress[1] + " " + latestAddress[2] + " " + latestAddress[3];

        return ResponseEntity.ok(goal);
    }

    //여행중인 유저 액티비티 추천
    @PostMapping("/ing/activity")
    public ResponseEntity<String> activityRecommendationsForTraveller() throws Exception{
        //로그인 정보에서 uid 받아오기
        Long userId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            userId = user.getUserId();
        }
        // 최신 주소 반환
        String[] latestAddress = yeobotService.findLatestAddress(userId);

        // 프롬프트 반환
        String goal = "Recommend me some fun things to do near " + latestAddress[0] + " " + latestAddress[1] + " " + latestAddress[2] + " today.";

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
