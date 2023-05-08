package com.moyeo.main.controller;

import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.GetPostRes;
import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.service.MoyeoPhotoService;
import com.moyeo.main.service.MoyeoPostService;
import com.moyeo.main.service.PhotoService;
import com.moyeo.main.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/auth/post")
@RestController
@Slf4j
public class PostController {
    private final PostService postService;
    private final PhotoService photoService;
    private final MoyeoPostService moyeoPostService;
    private final MoyeoPhotoService moyeoPhotoService;

    
    //포스트 등록 (Address 1 - 국가 -> Address 4 - 동네)
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPost(@RequestPart MultipartFile flagFile,
                                     @RequestPart List<MultipartFile> imageFiles,
                                     @RequestPart MultipartFile voiceFile,
                                     @Valid @ModelAttribute AddPostReq addPostReq,
                                     @RequestParam(required = false, defaultValue = "false") Boolean isMoyeo) throws Exception {
        // 입력 테스트중
        System.out.println("flagFile:" + flagFile);
        log.info("timeline id : {}", addPostReq.getTimelineId());
        log.info("국가 이름 : {}", addPostReq.getAddress1());
        if(isMoyeo == false) {
            Post savedPost = postService.createPost(addPostReq);

            List<Photo> photoList = photoService.createPhotoList(imageFiles, savedPost);
            postService.insertPost(savedPost, photoList, flagFile, voiceFile, addPostReq);

            // addPost 요청에 대한 응답으로 timelineId 반환
            Map<String, Object> res = new HashMap<>();
            res.put("timelineId", addPostReq.getTimelineId());
            return ResponseEntity.ok(res);
        } else {
            MoyeoPost savedPost = moyeoPostService.createPost(addPostReq);
            List<MoyeoPhoto> photoList = moyeoPhotoService.createPhotoList(imageFiles, savedPost);
            moyeoPostService.insertPost(savedPost, photoList, flagFile, voiceFile, addPostReq);

            // addPost 요청에 대한 응답으로 timelineId 반환
            Map<String, Object> res = new HashMap<>();
            res.put("timelineId", addPostReq.getTimelineId());
            return ResponseEntity.ok(res);
        }
    }

    //포스트 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) throws Exception {
        if (postId == null) {
            log.info("postId 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        postService.deletePostById(postId);
        return ResponseEntity.ok().build();
    }

    //메인페이지에서 포스트 조회
    @GetMapping("/main/{location}")
    public ResponseEntity<?> getPost(@PathVariable String location) throws Exception {
        if (location == null) {
            log.info("location 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        List<GetPostRes> getPostResList = postService.findByLocation(location);
        return ResponseEntity.ok(getPostResList);
    }

    //내 페이지에서 포스트 조회
    @GetMapping("/mine/{myLocation}")
    public ResponseEntity<?> getMyPost(@PathVariable String myLocation) throws Exception {
        // accessToken에서 user 가져오기
        Long userUid = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            userUid = user.getUserId();
        }

        // 응답
        if (myLocation == null) {
            log.info("location 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        List<GetPostRes> getPostResList = postService.findMyPost(myLocation, userUid);
        return ResponseEntity.ok(getPostResList);
    }
}
