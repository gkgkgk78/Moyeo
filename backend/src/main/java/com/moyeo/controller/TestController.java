package com.moyeo.controller;


import com.moyeo.dto.AddPostReq;
import com.moyeo.dto.MainTimelinePhotoDtoRes;
import com.moyeo.entity.Photo;
import com.moyeo.entity.Post;
import com.moyeo.service.PhotoService;
import com.moyeo.service.PostService;
import com.moyeo.service.TimeLineService;
import com.moyeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/test")
public class TestController {

    private final TimeLineService timeLineService;
    private final UserService userService;

    private final PostService postService;
    private final PhotoService photoService;

    @Operation(description = "타임라인 생성하는 컨트롤러")
    @PostMapping("")
    public ResponseEntity<?> makeTimeLine() throws Exception {
        //유저 한명을 받아 와서 해당 유저로 타임라인을 생성하고Y자 한다
        log.info("여행 시작 기능 시작");
        for(int i = 1;i<100000;i++){
            timeLineService.makenewTimelineTemp();
            for (int j = 0; j<9;j++){
                Post savedPost = postService.makePost();
                log.info("");
                List<Photo> photoList = photoService.createPhotoListTest(savedPost);
                postService.insertPostTest(savedPost, photoList, AddPostReq.builder().timelineId((long) i).address1("test").build());
            }
            timeLineService.changeTimelineFinish();
        }
        log.info("여행 시작 기능 종료");
        return new ResponseEntity<>(HttpStatus.OK);
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


}
