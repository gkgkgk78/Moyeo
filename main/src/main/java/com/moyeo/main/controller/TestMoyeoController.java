package com.moyeo.main.controller;


import com.moyeo.main.conponent.YeobotClient;
import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.ChatReq;
import com.moyeo.main.dto.GetPostRes;
import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.PostInsertReq;
import com.moyeo.main.entity.Chat;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.UserRepository;
import com.moyeo.main.service.*;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
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

    private final AsyncTestService asyncTestService;
    private final MessageBoxService messageBoxService;

    private final TimeLineServiceImpl timeLineServiceImpl;

    @GetMapping("/timeline")
    public ResponseEntity<?> testGetTimelineList() throws Exception {
        return ResponseEntity.ok(timeLineServiceImpl.getTimelineList2());
    }

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


    @PostMapping("/ing/restaurant")
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
        String result = yeobotClient.sendYeobotData("restaurant", goal);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @PostMapping("/ing/activity")
    public ResponseEntity<String> activityRecommendationsForTraveller() {
        log.info("여행중인 유저에게 액티비티추천 spring 내부 로직 시작");
        //로그인 정보에서 uid 받아오기
        List<String[]> latestAddress = new ArrayList<>();
        String[] lad = new String[4];
        lad[0] = new String("서울특별시");
        lad[1] = new String("강남구");
        lad[2] = new String("역삼동");
        lad[3] = new String("테헤란로");
        latestAddress.add(lad);

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

        //return ResponseEntity.ok(goal);

        String caseType = "activity";

        log.info("여행중인 유저에게 액티비티추천 spring 내부 로직 완료");

        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData(caseType, goal);


        return new ResponseEntity<>(result, HttpStatus.OK);

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

        //ResponseEntity<String> response = ResponseEntity.ok(goal);

        System.out.println("is it started?");

        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData("place", goal);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        System.out.println(user + " is user");

        log.info("response insert 작업 시작");

        chatService.insertResponse(user, result);
        log.info("몽고디비저장완료");
        messageBoxService.insertMessage(user.getUserId(), result);
        log.info("메시지박스저장완료");

        log.info("response insert 작업 완료");

        fcmService.send(user);

        return new ResponseEntity<>(result, HttpStatus.OK);
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
        String result = yeobotClient.sendYeobotData("activity", goal);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

//    @GetMapping("/firebase/message")//테스트 해보기
//    public ResponseEntity<?> getTimelineLatestWithPaging() throws Exception {
//        fcmService.send();
//        return new ResponseEntity<>(HttpStatus.OK);
//
//    }

    @PostMapping("/chat")
    public ResponseEntity<?> insertChat(@RequestBody ChatReq chat) throws Exception {

        //insert 작업의 첫번째 파라미터는 인증된 사용자의 고유한 닉네임 값이 들어갈 것임
//        Long l1 = 1l;

        //insert 작업의 첫번째 파라미터는 인증된 사용자의 고유한 닉네임 값이 들어갈 것임
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null){
            user = (User) auth.getPrincipal();
            log.info(user+"유저 찾았다");
        }

        System.out.println(user + " 유저 찾았다");


        log.info("chat insert작업 시작");
        //chatService.insert(l1.toString(), chat);

        chatService.insert(user.getUserId().toString(), chat);
        log.info("chat insert작업 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/chat/{name}")
    public ResponseEntity<?> selectChat(@PathVariable String name) throws Exception {

        List<Chat> result = chatService.select(name);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/notification")
    public ResponseEntity<?> toNotification(@RequestBody PostInsertReq post) throws Exception {
        log.info("notification 테스트 시작");
        asyncTestService.test(post);
        log.info("notification 테스트 종료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/notification1")
    public ResponseEntity<?> toNotification1(@RequestBody PostInsertReq post) throws Exception {
        log.info("notification 테스트 시작");
        asyncTestService.test(post);
        log.info("notification 테스트 종료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/ing/pushtestrestaurant")
    public ResponseEntity<String> batchPushTestForRestaurant() throws Exception {

        List<String[]> latestAddress = new ArrayList<>();
        String[] lad = new String[4];
        lad[0] = new String("Osaka");
        lad[1] = new String("Osaka-shi");
        lad[2] = new String("Chuo-ku");
        lad[3] = new String("Osakajo");
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
//        ResponseEntity<String> response = ResponseEntity.ok(goal);
        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData("pushdining", goal);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }


    @PostMapping("/ing/pushtestactivity")
    public ResponseEntity<String> batchPushTestForActivity() throws Exception {

        List<String[]> latestAddress = new ArrayList<>();
        String[] lad = new String[4];
        lad[0] = new String("부산광역시");
        lad[1] = new String("수영구");
        lad[2] = new String("광안동");
        lad[3] = new String("");
        latestAddress.add(lad);

        // 프롬프트 반환
        List<String> addressList = new ArrayList<>();

        for (String[] addresses : latestAddress) {
            for (String address : addresses) {
                addressList.add(String.valueOf(address));
            }
        }

        String goal = "Recommend me some fun things to do near " + addressList.get(0) + " " + addressList.get(1) + " " + addressList.get(2) +  " now.";
        //return ResponseEntity.ok(goal);
//        ResponseEntity<String> response = ResponseEntity.ok(goal);
        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData("pushactivity", goal);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    @PostMapping("/ing/pushnextarrival")
    public ResponseEntity<String> batchPushTestForNextArrival() throws Exception {

        List<String[]> latestAddress = new ArrayList<>();
        String[] lad = new String[4];
        lad[0] = new String("New York City");
        lad[1] = new String("from Central Park West to 5th Avenue");
        lad[2] = new String("59th to 110th Street Manhattan Borough");
        lad[3] = new String("");
        latestAddress.add(lad);

        // 프롬프트 반환
        List<String> addressList = new ArrayList<>();

        for (String[] addresses : latestAddress) {
            for (String address : addresses) {
                addressList.add(String.valueOf(address));
            }
        }

        String goal = "Recommend me good place to visit near " + addressList.get(0) + " " + addressList.get(1) + ".";
        //return ResponseEntity.ok(goal);
//        ResponseEntity<String> response = ResponseEntity.ok(goal);
        // Flask 서버에 데이터 전송
        String result = yeobotClient.sendYeobotData("pushnextarrival", goal);
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

}
