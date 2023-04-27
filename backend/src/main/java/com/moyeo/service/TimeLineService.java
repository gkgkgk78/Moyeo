package com.moyeo.service;

import com.moyeo.dto.MainTimelinePhotoDtoRes;
import com.moyeo.dto.TimelinePostOuter;
import com.moyeo.entity.TimeLine;
import com.moyeo.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TimeLineService {

    //메인피드에서 최신순으로 조회=>완료
    List<TimeLine> searchTimelineOrderBylatest() throws Exception;

    //내 피드에서 내 타임라인 리스트 조회 ,
    List<TimeLine> searchMyTimeline(Long uid, User user) throws Exception;

    //다른 유저의 피드에서 타임라인 조회도 가능
    List<TimeLine> searchTimelineNotPublic(Long uid) throws Exception;

    //타임라인 한개 조회=>완료
    TimelinePostOuter searchOneTimeline(Long uid,User user) throws Exception;

    //여행시작
    Long makenewTimeline(User user) throws Exception;

    //여행끝
    void finishTimeline(Long uid,String title,User user) throws Exception;

    //타임라인삭제=>우선 놔둠
    void deleteTimeline(Long uid,User user) throws Exception;

    //타임라인 공개 <->비공개 변경 => 완료
    Boolean changePublic(Long uid,User user) throws Exception;


    //메인 피드상에서 타임라인 페이징 처리해서 조회하는 메서드
    List<MainTimelinePhotoDtoRes> searchTimelineOrderBylatestPaging(Pageable pageable) throws Exception;

    //내타임라인 페이징 처리해서 조회
    List<MainTimelinePhotoDtoRes> searchMyTimelineWithPaging(User user, Pageable pageable) throws Exception;

    //다른 유저의 피드에서 타임라인 조회 with Paging
    List<MainTimelinePhotoDtoRes> searchTimelineNotPublicWithPaging(Long uid, Pageable pageable) throws Exception;


    //타임 라인 하나 불러올시에, 썸네일, 시작 위치 끝나는 위치,
    TimeLine isTraveling(Long uid);

    void makenewTimelineTemp();


    void changeTimelineFinish();


}
