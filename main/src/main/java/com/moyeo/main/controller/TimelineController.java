package com.moyeo.main.controller;

import com.moyeo.main.conponent.ClovaSpeechClient;
import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.TimelinePostOuter;
import com.moyeo.main.entity.User;
import com.moyeo.main.service.TimeLineService;
import com.moyeo.main.service.TimeLineService2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/api/auth/timeline")
@RestController
@Log4j2
@Tag(name = "Timeline")
public class TimelineController {
    private final TimeLineService timeLineService;
    private final TimeLineService2 timeLineService2;

    //타임라인 한개 조회 => 이제 이걸 해야함 , 넘겨줄때 여행한 국가 리스트 순서대로 해서 만들어 넘겨주면 될듯
    @GetMapping("/{uid}")
    @Operation(summary = "타임라인 상세 조회")
    public ResponseEntity<?> seleteOneTimeLine(@PathVariable Long uid) throws Exception {
        log.info("타임라인 한개 조회 시작");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        // TimelinePostOuter timeline = timeLineService.searchOneTimeline(uid, user);
        TimelinePostOuter timeline = timeLineService2.searchOneTimeline(uid, user);
        if (timeline==null)
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        log.info("timeLine info :{}",timeline.getTimeline());
        log.info("타임라인 한개 조회 종료");
        return new ResponseEntity<>(timeline, HttpStatus.OK);
    }

    //여행시작 , 여기에는 사용자 를 구분할수 있는 requestbody가 필요하다
    @PostMapping("")
    @Operation(summary = "타임라인 시작 (여행 시작!)")
    public ResponseEntity<?> makeTimeLine() throws Exception {
        //유저 한명을 받아 와서 해당 유저로 타임라인을 생성하고자 한다
        log.info("여행 시작 기능 시작");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        Long now=timeLineService.makenewTimeline(user);
        log.info("여행 시작 기능 종료");
        return new ResponseEntity<>(now,HttpStatus.OK);

    }


    //여행끝
    @PutMapping("/{uid}/{title}")
    @Operation(summary = "타임라인 종료")
    public ResponseEntity<?> finishTimeLine(@PathVariable Long uid, @PathVariable String title) throws Exception {
        log.info("여행종료 기능 시작");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        timeLineService.finishTimeline(uid, title,user);
        log.info("여행종료 기능 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //타임라인 공개 <->비공개 변경
    @PutMapping("/switch/{uid}")
    @Operation(summary = "타임라인 공개 여부 수정")
    public ResponseEntity<?> changeTimeLinePublic(@PathVariable Long uid) throws Exception {
        log.info("타임라인 공개<->비공개 전환 시작");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        Boolean check=timeLineService.changePublic(uid,user);
        log.info("타임라인 공개<->비공개 전환 종료");

        return new ResponseEntity<>(check,HttpStatus.OK);
    }

    //타임라인삭제
    @DeleteMapping("/{uid}")
    @Operation(summary = "타임라인 삭제")
    public ResponseEntity<?> deleteTimeLine(@PathVariable Long uid) throws Exception {
        log.info("타임라인 삭제 기능 시작");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        timeLineService.deleteTimeline(uid,user);
        log.info("타임라인 삭제 기능 종료");
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /*paging  하는 메서드들*/

    //메인피드 최신순 타임라인 조회 with paging +
    //어떤 유저로 받을지는 파라미터에 추가가 되어야 함
    //sort="id", direction = Sort.Direction.DESC
    @GetMapping("/main/{page}")//테스트 해보기
    @Operation(summary = "타임라인 조회 (메인 페이지에서)", description = "최신순으로 조회된다. with paging")
    public ResponseEntity<?> getTimelineLatestWithPaging(@PathVariable Integer page) throws Exception {
        log.info("메인피드 최신순 타임라인 조회 시작");
        Pageable pageable = PageRequest.of(page, 15, Sort.by("createTime").descending());
        List<MainTimelinePhotoDtoRes> timelinelist = timeLineService.searchTimelineOrderBylatestPaging(pageable);
        log.info("메인피드 최신순 타임라인 조회 종료");
        if (timelinelist != null) {
            return new ResponseEntity<>(timelinelist, HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

    }


    //내 피드에서 내 타임라인 리스트 조회 with paging =>테스트 해보기
    @GetMapping("/mine/{page}")
    @Operation(summary = "타임라인 조회 (내 페이지에서)", description = "최신순으로 조회된다. with paging")
    public ResponseEntity<?> getMyTimelineListWithPaging(@PathVariable Integer page) throws Exception {
        log.info("내 피드에서 내 타임라인 리스트 조회 기능 시작");
        Pageable pageable = PageRequest.of(page, 15);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
        List<MainTimelinePhotoDtoRes> timelinelist = timeLineService.searchMyTimelineWithPaging(user, pageable);
        log.info("내 피드에서 내 타임라인 리스트 조회 기능 종료");
        return new ResponseEntity<>(timelinelist, HttpStatus.OK);
    }


    //다른 유저의 피드에서 타임라인 조회 with Paging => 테스트 해보기
    @GetMapping("/other/{uid}/{page}")
    @Operation(summary = "타임라인 조회 (다른 유저의 피드에서)", description = "최신순으로 조회된다. with paging")
    public ResponseEntity<?> getAnotherTimelineListWithPaging(@PathVariable Long uid, @PathVariable Integer page) throws Exception {
        Pageable pageable = PageRequest.of(page, 15);
        log.info("다른 유저의 피드에서 타임라인 조회 기능 시작");
        List<MainTimelinePhotoDtoRes> timelinelist = timeLineService.searchTimelineNotPublicWithPaging(uid, pageable);
        log.info("다른 유저의 피드에서 타임라인 조회 기능 종료");
        return new ResponseEntity<>(timelinelist, HttpStatus.OK);

    }


}
