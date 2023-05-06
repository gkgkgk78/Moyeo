package com.moyeo.main.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moyeo.main.conponent.YeobotClient;
import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.ChangeFavoriteStatusReq;
import com.moyeo.main.dto.ChangeFavoriteStatusRes;
import com.moyeo.main.dto.GetPostRes;
import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.MoyeoMembersReq;
import com.moyeo.main.dto.TimelinePostOuter;
import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.UserRepository;
import com.moyeo.main.service.FavoriteService;
import com.moyeo.main.service.MoyeoMembersService;
import com.moyeo.main.service.MoyeoPhotoService;
import com.moyeo.main.service.MoyeoPostService;
import com.moyeo.main.service.MoyeoTimeLineService;
import com.moyeo.main.service.PhotoService;
import com.moyeo.main.service.PostService;
import com.moyeo.main.service.TimeLineService;
import com.moyeo.main.service.TimeLineService2;
import com.moyeo.main.service.UserService;
import com.moyeo.main.service.YeobotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "MoyeoTest", description = "동행 테스트 API Document")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/moyeoTest")
public class TestAccompanyController {
    private final TimeLineService timeLineService;
    private final UserService userService;
    private final PostService postService;
    private final PhotoService photoService;
    private final TimeLineService2 timeLineService2;
    private final UserRepository userRepository;
    private final MoyeoPostService moyeoPostService;
    private final MoyeoPhotoService moyeoPhotoService;
    private final MoyeoTimeLineService moyeoTimeLineService;
    private final MoyeoMembersService moyeoMembersService;
    private final FavoriteService favoriteService;
    @PostMapping("/moyeotimeline/{userId}")
    @Operation(summary = "동행 여행 시작", description = "동행 여행 시작 -> 동행 타임라인 생성")
    // @Operation(summary = "동행 여행 시작", description = "동행 여행 시작 -> 동행 타임라인 생성", tags = {"View"})
    public ResponseEntity<?> registMoyeoTimeline(@PathVariable Long userId) throws Exception { // 동행 시작
        User user = userRepository.getByUserId(userId);

        return ResponseEntity.ok(moyeoTimeLineService.registMoyeoTimeline(user));
    }

    @PostMapping("/moyeomembers")
    @Operation(summary = "동행 합류", description = "동행 합류")
    public ResponseEntity<?> registMoyeoMembers(@RequestBody MoyeoMembersReq moyeoTimelineId) throws Exception {
        User user = userRepository.getByUserId(moyeoTimelineId.getUserId());

        return ResponseEntity.ok(moyeoMembersService.registMoyeoMembers(user, moyeoTimelineId.getMoyeoTimelineId()));
    }

    @PutMapping("/moyeomembers")
    @Operation(summary = "동행 나감", description = "동행 나감")
    public ResponseEntity<?> updateMoyeoMembers(@RequestBody MoyeoMembersReq moyeoTimelineId) throws Exception {
        User user = userRepository.getByUserId(moyeoTimelineId.getUserId());

        return ResponseEntity.ok(moyeoMembersService.updateMoyeoMembers(user, moyeoTimelineId.getMoyeoTimelineId()));
    }

    @GetMapping("/timeline/{uid}/{userId}")
    @Operation(summary = "타임라인 상세 조회", description = "타임라인 상세 조회")
    public ResponseEntity<?> seleteOneTimeLine(@PathVariable Long uid, @PathVariable Long userId) throws Exception {
        log.info("타임라인 한개 조회 시작");
        User user = userRepository.getByUserId(userId);
        TimelinePostOuter timeline = timeLineService2.searchOneTimeline(uid, user);
        if (timeline==null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        log.info("timeLine info :{}",timeline.getTimeline());
        log.info("타임라인 한개 조회 종료");
        return new ResponseEntity<>(timeline, HttpStatus.OK);
    }

    //포스트 등록 (Address 1 - 국가 -> Address 4 - 동네)
    @Operation(summary = "포스트 등록", description = "포스트 등록")
    @PostMapping(value = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    // 포스트 수정 (공개 여부 수정)
    @PutMapping("/moyeopost/{moyeoPostId}/{userId}")
    @Operation(summary = "포스트 공개 여부 수정", description = "")
    public ResponseEntity<?> updateMoyeoPost(@PathVariable Long moyeoPostId, @PathVariable Long userId) throws Exception {
        if (moyeoPostId == null) {
            log.info("moyeoPostId 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        User user = userRepository.getByUserId(userId);

        moyeoPostService.updateMoyeoPost(user, moyeoPostId);
        return ResponseEntity.ok().build();
    }

    // 포스트 삭제
    @DeleteMapping("/moyeopost/{moyeoPostId}/{userId}")
    @Operation(summary = "모여 포스트 삭제", description = "")
    public ResponseEntity<?> deleteMoyeoPost(@PathVariable Long moyeoPostId, @PathVariable Long userId) throws Exception {
        if (moyeoPostId == null) {
            log.info("moyeoPostId 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        User user = userRepository.getByUserId(userId);

        moyeoPostService.deleteMoyeoPost(user, moyeoPostId);
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/moyeofavorite/{userId}")
    @Operation(summary = "모여 포스트 좋아요 기능", description = "")
    public ResponseEntity<?> changeFavoriteStatus(@RequestBody @Valid ChangeFavoriteStatusReq changeFavoriteStatusReq
        , @RequestParam(required = false, defaultValue = "false") Boolean isMoyeo, @PathVariable Long userId) throws Exception {
        // accessToken에서 userUid 값 가져오기
        User user = userRepository.getByUserId(userId);

        // 응답
        ChangeFavoriteStatusRes res = new ChangeFavoriteStatusRes();
        res.setPostId(changeFavoriteStatusReq.getPostId());
        res.setMoyeo(isMoyeo);
        res.setFavorite(favoriteService.isFavorite(changeFavoriteStatusReq.getPostId(), userId, isMoyeo));
        res.setTotalFavorite(favoriteService.countFavorite(changeFavoriteStatusReq.getPostId(), isMoyeo));

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/moyeofavorite")
    @Operation(summary = "내가 좋아요 누른 포스트 보기", description = "")
    public ResponseEntity<?> getFavoritePostList(@RequestParam Long userUid) throws Exception {
        // List<Post> favoritePostList = favoriteService.findFavoritePost(userUid);
        return ResponseEntity.ok(favoriteService.findFavoritePost(userUid));
    }

    //메인페이지에서 포스트 조회
    @GetMapping("/moyeopost/main/{location}")
    @Operation(summary = "포스트 - 메인 페이지에서 포스트 address로 검색", description = "최신순")
    public ResponseEntity<?> getPost(@PathVariable String location) throws Exception {
        if (location == null) {
            log.info("location 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        List<GetPostRes> getPostResList = postService.findByLocation(location);
        return ResponseEntity.ok(getPostResList);
    }

    //내 페이지에서 포스트 조회
    @GetMapping("/moyeopost/mine/{myLocation}/{userUid}")
    @Operation(summary = "포스트 - 내 페이지에서 포스트 address로 검색", description = "최신순")
    public ResponseEntity<?> getMyPost(@PathVariable String myLocation, @PathVariable Long userUid) throws Exception {
        if (myLocation == null) {
            log.info("location 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        List<GetPostRes> getPostResList = postService.findMyPost(myLocation, userUid);
        return ResponseEntity.ok(getPostResList);
    }

}
