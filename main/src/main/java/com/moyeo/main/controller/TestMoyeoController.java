package com.moyeo.main.controller;


import com.moyeo.main.conponent.YeobotClient;
import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.TravelRecommendRequest;
import com.moyeo.main.entity.Chat;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.service.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test")
public class TestMoyeoController {

    private final TimeLineService timeLineService;
    private final UserService userService;
    private final PostService postService;
    private final PhotoService photoService;

    private final YeobotService yeobotService;

    private final YeobotClient yeobotClient;

    private final FcmService fcmService;

    private final ChatService chatService;


    @PostMapping("/login")
    public ResponseEntity<?> signUpKakao() throws Exception {
        Boolean boo = userService.signUp();
        return new ResponseEntity<>(boo, HttpStatus.OK);
    }


    //포스트 등록 (Address 1 - 국가 -> Address 4 - 동네)
    @PostMapping(value = "/post")
    public ResponseEntity<?> addPost(@RequestBody AddPostReq addPostReq) throws Exception {
        Post savedPost = postService.createPost(addPostReq);
        List<Photo> photoList = photoService.createPhotoListTest(savedPost);
        postService.insertPostTest(savedPost, photoList, addPostReq);
        // addPost 요청에 대한 응답으로 timelineId 반환
        Map<String, Object> res = new HashMap<>();
        res.put("timelineId", addPostReq.getTimelineId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/main/{page}")//테스트 해보기
    public ResponseEntity<?> getTimelineLatestWithPaging(@PathVariable Integer page) throws Exception {
        log.info("메인피드 최신순 타임라인 조회 시작");
        Pageable pageable = PageRequest.of(page, 15, Sort.by("createTime").descending());
        List<MainTimelinePhotoDtoRes> timelinelist = timeLineService.searchTimelineOrderBylatestPaging(pageable);
        log.info("메인피드 최신순 타임라인 조회 종료");
        if (timelinelist != null) {
            return new ResponseEntity<>(timelinelist, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

    }


    @PutMapping("")
    public ResponseEntity<?> convertAll() throws Exception {
        log.info("전체 타임라인 완료 변환 시작");
        timeLineService.changeTimelineFinish();
        log.info("전체 타임라인 완료 변환 종료");
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @PostMapping("/ing/dining")
    public ResponseEntity<String> restaurantRecommendations() throws Exception {
        //로그인 정보에서 uid 받아오기
//        Long userId = null;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.getPrincipal() != null) {
//            User user = (User) auth.getPrincipal();
//            userId = user.getUserId();
//        }
        // 최신 주소 반환
        //List<String[]> latestAddress = yeobotService.findLatestAddress(userId);
        List<String[]> latestAddress = new ArrayList<>();
        String[] lad = new String[4];
        lad[0] = new String("서울특별시");
        lad[1] = new String("강남구");
        lad[2] = new String("역삼동");
        lad[3] = new String("테헤란로");
        latestAddress.add(lad);

        // 프롬프트 반환
        List<String> addressList = new ArrayList<>();

        for (String[] addresses : latestAddress) {
            for (String address : addresses) {
                addressList.add(String.valueOf(address));
            }
        }

        String goal = "Search for a good restaurant near " + addressList.get(0) + " " + addressList.get(1) + " " + addressList.get(2) + " " + addressList.get(3) + ".";
        //return ResponseEntity.ok(goal);
        ResponseEntity<String> response = ResponseEntity.ok(goal);
        // Flask 서버에 데이터 전송
        yeobotClient.sendYeobotData("dining", goal);
        return response;

    }


    //여행중이 아닌 유저 여행지 추천
    @PostMapping("/yet/place")
    public ResponseEntity<String> recommendPlace() throws IOException {
        // request에서 필요한 정보를 추출해서 변수에 저장
        String destination = "유럽";
        String season = "여름";
        String purpose = "1주일 동안";

        //
        String goal = "Recommend me a good place for travel to go in " + destination + " in " + season + " for " + purpose + ".";

        // 프롬프트 반환
        //return ResponseEntity.ok(goal);

        ResponseEntity<String> response = ResponseEntity.ok(goal);

        // Flask 서버에 데이터 전송
        yeobotClient.sendYeobotData("place", goal);

        return response;
    }


    //여행중이 아닌 유저 액티비티 추천
    @PostMapping("/yet/activity")
    public ResponseEntity<String> recommendActivities() throws IOException {
        // request에서 필요한 정보를 추출해서 변수에 저장
        String destination = "경상북도 경주";
        String season = "가을";

        // 추천 결과 문자열 생성
        String goal = "Recommend me some fun things to do near " + destination + " during " + season + ".";

        // 프롬프트 반환
        //return ResponseEntity.ok(goal);

        ResponseEntity<String> response = ResponseEntity.ok(goal);

        // Flask 서버에 데이터 전송
        yeobotClient.sendYeobotData("activity", goal);

        return response;

    }

    @GetMapping("/firebase/message")//테스트 해보기
    public ResponseEntity<?> getTimelineLatestWithPaging() throws Exception {


        fcmService.send();

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("/chat")
    public ResponseEntity<?> insertChat(@RequestBody Chat chat) throws Exception {

        //insert 작업의 첫번째 파라미터는 인증된 사용자의 고유한 닉네임 값이 들어갈 것임

        chatService.insert("mongotest", chat);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/chat/{name}")
    public ResponseEntity<?> selectChat(@PathVariable String name) throws Exception {

        List<Chat> result = chatService.select(name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
