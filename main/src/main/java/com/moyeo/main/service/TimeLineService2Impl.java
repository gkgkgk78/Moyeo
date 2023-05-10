package com.moyeo.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.moyeo.main.dto.BasePostDto;
import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.MyPostDtoRes;
import com.moyeo.main.dto.PostMembers;
import com.moyeo.main.dto.TimelinePostInner;
import com.moyeo.main.dto.TimelinePostOuter;
import com.moyeo.main.entity.Favorite;
import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.Nation;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.TimeLineAndMoyeo;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.FavoriteRepository;
import com.moyeo.main.repository.MoyeoFavoriteRepository;
import com.moyeo.main.repository.MoyeoMembersRepository;
import com.moyeo.main.repository.MoyeoPostRepository;
import com.moyeo.main.repository.MoyeoPublicRepository;
import com.moyeo.main.repository.NationRepository;
import com.moyeo.main.repository.PhotoRepository;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.TimeLineAndMoyeoRepository;
import com.moyeo.main.repository.TimeLineRepository;
import com.moyeo.main.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class TimeLineService2Impl implements TimeLineService2 {
    private final TimeLineRepository timeLineRepository;
    private final PostRepository postRepository;
    private final MoyeoPostRepository moyeoPostRepository;
    private final TimeLineAndMoyeoRepository timeLineAndMoyeoRepository;
    private final FavoriteRepository favoriteRepository;
    private final MoyeoFavoriteRepository moyeoFavoriteRepository;
    private final MoyeoPublicRepository moyeoPublicRepository;
    private final UserRepository userRepository;
    private final UtilService utilService;


    @Override
    public TimelinePostOuter searchOneTimeline(Long uid, User user) throws BaseException {

        // 해당되는 타임라인을 얻어 왔고
        TimeLine timeLine = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        // 이제 그 다음으로 해당 되는 타임라인을 포스트를 얻어 올거임
        // List<Post> postList = postRepository.findAllByTimelineId(timeLine);

        User timelineUser = timeLine.getUserId();
        Boolean isMine = timelineUser.getUserId().equals(user.getUserId());

        // <일반 포스트 리스트 가져오기>
        List<Post> posts = postRepository.findAllByTimelineId(timeLine);
        // if(posts == null || posts.size() == 0) return addPostToResponse(timeLine, null, isMine);
        List<BasePostDto> postList = new ArrayList<>();
        if(posts != null && posts.size() != 0) {
            log.info("일반 포스트 가져오기...");
            postList = posts.stream()
                .map(post -> {
                    Boolean isFavorite = false;
                    if(favoriteRepository.findFirstByPostIdAndUserId(post, user) != null) {
                        isFavorite = true;
                    }
                    return BasePostDto.builder(post, isFavorite).build();
                })
                .collect(Collectors.toList());
        }


        // 모여 타임라인 id 리스트
        List<Long> moyeoTimelineIdList = timeLineAndMoyeoRepository.findAllMoyeoTimelineIdByTimlineId(timeLine.getTimelineId()).orElse(null);
        if(moyeoTimelineIdList == null) {
            return addPostToResponse(timeLine, postList, isMine);
        }

        // <모여 포스트 리스트도 가져오기!>
        List<MoyeoPost> moyeoPosts = moyeoPostRepository.findAllByMoyeoTimelineIdIn(moyeoTimelineIdList);
        if(moyeoPosts == null || moyeoPosts.size() == 0) {
            return addPostToResponse(timeLine, postList, isMine);
        }
        log.info("모여 포스트 리스트 가져오기...");
        List<BasePostDto> moyeoPostList = moyeoPosts.stream()
                .filter(post -> {
                    MoyeoPublic moyeoPublic = moyeoPublicRepository.findFirstByUserIdAndMoyeoPostId(timelineUser, post);
                    if(moyeoPublic == null) return false;
                    if(isMine) { // 내 타임라인을 조회하는 거라면 삭제된 포스트는 제외하고 가져와야 한다.
                        return !moyeoPublic.getIsDeleted();
                    }
                    return !moyeoPublic.getIsDeleted() && moyeoPublic.getIsPublic(); // 타인의 타임라인을 조회하는 거라면 해당 타임라인 유저가 공개한 것만 가져와야 한다.
                })
                .map(post -> {
                    Boolean isFavorite = false;
                    if(moyeoFavoriteRepository.findFirstByMoyeoPostIdAndUserId(post, user) != null) {
                        isFavorite = true;
                    }

                    return BasePostDto.builder(post
                        , isFavorite
                        , moyeoPublicRepository.findByMoyeoPostId(post).stream().map(PostMembers::new).collect(Collectors.toList()))
                        .build();
                })
                .collect(Collectors.toList());

        // 합쳐서 createTime으로 정렬
        postList.addAll(moyeoPostList);
        Collections.sort(postList, Comparator.comparing(BasePostDto::getCreateTime));


        // <일반 포스트 리스트 가져오기>
        // List<Post> posts = postRepository.findAllByTimelineId(timeLine);
        // if(posts == null) return addPostToResponse(timeLine, null, isMine);
        //
        // Map<Long, Integer> postIdMap = new LinkedHashMap<>(); // <postId, postList의 index>
        //
        // List<BasePostDto> postList = IntStream.range(0, posts.size())
        //     .mapToObj(index -> {
        //         Post post = posts.get(index);
        //         postIdMap.put(post.getPostId(), index);
        //
        //         Boolean isFavorite = false;
        //         if(favoriteRepository.findFirstByPostIdAndUserId(post, user) != null) {
        //             isFavorite = true;
        //         }
        //         return BasePostDto.builder(post, isFavorite).build();
        //     })
        //     .collect(Collectors.toList());
        // <모여 포스트 리스트 가져오기> XXXXXXXXXXXXXXXXXXX
        // // 1. timeLineAndMoyeo
        // List<TimeLineAndMoyeo> timeLineAndMoyeoList = timeLineAndMoyeoRepository.findAllByTimelineId(timeLine);
        // if(timeLineAndMoyeoList == null) {
        //     return addPostToResponse(timeLine, postList, isMine);
        // }
        //
        // int index = 0; //
        // Long tempMoyeoTimelineId = 0L;
        // List<MoyeoMembers> moyeoMembersList = null;
        // int amount = 0;
        // // int lastPosition = 0;
        // for(TimeLineAndMoyeo timeLineAndMoyeo: timeLineAndMoyeoList) {
        //     MoyeoTimeLine moyeoTimeLine = timeLineAndMoyeo.getMoyeoTimelineId();
        //     Long moyeoTimelineId = moyeoTimeLine.getMoyeoTimelineId();
        //
        //     if(!tempMoyeoTimelineId.equals(moyeoTimelineId)) { // 이전과 다른 모여 타임라인이라면
        //         index = 0;
        //         tempMoyeoTimelineId = moyeoTimelineId;
        //         // 2. moyeoMembers 가져오기
        //         moyeoMembersList = moyeoMembersRepository.findAllByMoyeoTimelineId(tempMoyeoTimelineId);
        //     }
        //
        //     LocalDateTime createTime = moyeoMembersList.get(index).getJoinTime();
        //     LocalDateTime finishTime = moyeoMembersList.get(index).getFinishTime();
        //     index++;
        //     if(finishTime == null) break;
        //     if(createTime == null) continue;
        //
        //     // 3. 현재 timeLineAndMoyeo에 해당하는 모여 포스트 리스트 가져오기
        //     List<BasePostDto> moyeoPostList = moyeoPostRepository.findAllByMoyeoTimelineIdAndCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(moyeoTimeLine, createTime, finishTime).stream()
        //         .filter(post -> {
        //             MoyeoPublic moyeoPublic = moyeoPublicRepository.findByUserIdAndMoyeoPostId(timelineUser, post);
        //             if(isMine) { // 내 타임라인을 조회하는 거라면 삭제된 포스트는 제외하고 가져와야 한다.
        //                 return !moyeoPublic.getIsDeleted();
        //             }
        //             return !moyeoPublic.getIsDeleted() && moyeoPublic.getIsPublic(); // 타인의 타임라인을 조회하는 거라면 해당 타임라인 유저가 공개한 것만 가져와야 한다.
        //         })
        //         .map(post -> {
        //             Boolean isFavorite = false;
        //             if(moyeoFavoriteRepository.findFirstByMoyeoPostIdAndUserId(post, user) != null) {
        //                 isFavorite = true;
        //             }
        //
        //             return BasePostDto.builder(post
        //                 , isFavorite
        //                 , moyeoPublicRepository.findByMoyeoPostId(post).stream().map(PostMembers::new).collect(Collectors.toList()))
        //                 .build();
        //         })
        //         .collect(Collectors.toList());
        //
        //     // 4. postList와 합치기
        //     if(moyeoPostList == null) continue;
        //
        //     Long orderPostId = timeLineAndMoyeo.getLastPostOrderNumber(); // orderPostId에 해당하는 일반 포스트 뒤에 넣어줘야 한다.
        //     Integer position = postIdMap.get(orderPostId);
        //     if(position == null) {
        //         position = findPosition(postIdMap, orderPostId);
        //     }
        //     postList.addAll(position + 1 + amount, moyeoPostList);
        //
        //     amount += moyeoPostList.size();
        //     // lastPosition = position + 1 + amount;
        // }


        return addPostToResponse(timeLine, postList, isMine);
    }

    public TimelinePostOuter addPostToResponse(TimeLine timeLine, List<BasePostDto> postList, Boolean isMine) {
        // TimelinePostOuter
        TimelinePostOuter timelinePostOuter = new TimelinePostOuter();
        timelinePostOuter.setIsComplete(timeLine.getIsComplete());
        timelinePostOuter.setIsPublic(timeLine.getIsTimelinePublic());
        timelinePostOuter.setIsMine(isMine);
        if(postList == null || postList.size() == 0) {
            return timelinePostOuter;
        }
        timelinePostOuter.setTitle(timeLine.getTitle());

        // TimelinePostInner
        TimelinePostInner timelinePostInner = new TimelinePostInner();
        String tempNationName = null; // 그 전에 국가 이름이 존재하지 않는지 파악하기 위해

        for(BasePostDto post: postList) {
            String NationName = post.getAddress1();

            if (tempNationName == null || !tempNationName.equals(NationName)) { // 해당 부분은 여행 국가가 새로 나타난 형태를 의미를 함
                if(tempNationName != null) timelinePostOuter.getTimeline().add(timelinePostInner);

                tempNationName = NationName;

                timelinePostInner = new TimelinePostInner();
                timelinePostInner.setFlag(post.getNationId().getNationUrl());
                timelinePostInner.setNation(tempNationName);
                if(post.getCreateTime() != null) timelinePostInner.setStartDate(utilService.invertLocalDate(post.getCreateTime()));
                timelinePostInner.setPostList(new ArrayList<>());
            }
            timelinePostInner.setFinishDate(utilService.invertLocalDate(post.getCreateTime()));

            timelinePostInner.getPostList().add(MyPostDtoRes.builder(post).build());
        }
        timelinePostOuter.getTimeline().add(timelinePostInner); // 마지막 timelinePostInner도 넣어주기

        return timelinePostOuter;
    }

    public List<MainTimelinePhotoDtoRes> testGetTimlineList(Long lastTimelineId) throws BaseException {
        // 메인 페이지에서 타임라인 목록 가져오기 (최신순으로, 페이징, 15개 가져오기)
        // 최신 순으로, isComplete, isTimelinePublic. 15개, but 마지막 페이지인지 판단하기 위해서 일단 16개 가져왔다..
        List<TimeLine> timeLineList = new ArrayList<>();
        if(lastTimelineId == null) { // 첫 페이지
            timeLineList = timeLineRepository
                .findTop16ByIsCompleteAndIsTimelinePublicOrderByTimelineIdDesc(true, true)
                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE_PAGING));
        } else {
            timeLineList = timeLineRepository
                .findTop16ByIsCompleteAndIsTimelinePublicAndTimelineIdLessThanOrderByTimelineIdDesc(true, true, lastTimelineId)
                .orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE_PAGING));
        }

        Boolean isLastPage = false;
        int size = timeLineList.size();
        log.info("size: {}", size);
        if(size < 16) isLastPage = true;
        log.info("isLastPage: {}", isLastPage);

        int len = isLastPage ? size : size - 1;
        log.info("len: {}", len);

        List<MainTimelinePhotoDtoRes> list = new ArrayList<>(); // 넘겨줄 timeline dto생성

        TimeLine timeLine;
        for(int i = 0; i < len; i++) {
            timeLine = timeLineList.get(i);

            // 첫번째 post
            Post firstPost = postRepository.findTopByTimelineId(timeLine);
            // 마지막 post
            Post lastPost = postRepository.findTopByTimelineIdOrderByPostIdDesc(timeLine);

            // moyeo post 가져오기
            // (1) timeline_and_moyeo 에서 timelineId로 moyeo_timeline_id 리스트 가져오기
            List<Long> moyeoTimelineIdList = timeLineAndMoyeoRepository.findAllMoyeoTimelineIdByTimlineId(timeLine.getTimelineId()).orElse(null);
            // (2-1) moyeo_post에서 moyeoTimelineIdList로 첫번째 moyeo post, 마지막 moyeo post 가져오기
            // (2-2) moyeo_post 조건: 모두가 공개로 설정 && 삭제되지 않은
            MoyeoPost firstMoyeoPost = moyeoPostRepository.findFirstVisiblePost(moyeoTimelineIdList);
            MoyeoPost lastMoyeoPost = moyeoPostRepository.findLastVisiblePost(moyeoTimelineIdList);


            // if ((firstPost == null || lastPost == null) && (firstMoyeoPost == null || lastMoyeoPost == null))
            //     continue;
            // if(firstPost == null && firstMoyeoPost == null)
            //     continue;
            // if(lastPost == null && lastMoyeoPost == null)
            //     continue;
            // TODO post 없어도 그냥 보여주기~~

            String thumbnailUrl;
            String startPlace;
            String lastPlace;

            if(firstMoyeoPost == null || firstPost != null && firstPost.getCreateTime().isBefore(firstMoyeoPost.getCreateTime())) {
                thumbnailUrl = firstPost.getPhotoList().get(0).getPhotoUrl();
                startPlace = firstPost.getAddress2();
            } else {
                thumbnailUrl = firstMoyeoPost.getMoyeoPhotoList().get(0).getPhotoUrl();
                startPlace = firstMoyeoPost.getAddress2();
            }

            if(lastMoyeoPost == null || lastPost != null && lastPost.getCreateTime().isAfter(lastMoyeoPost.getCreateTime())) {
                lastPlace = lastPost.getAddress2();
            } else {
                lastPlace = lastMoyeoPost.getAddress2();
            }

            User user = userRepository.findById(timeLine.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(timeLine, user, thumbnailUrl, startPlace, lastPlace).build());
        }

        log.info("len: {}", len);
        Long nowLastTimelineId = timeLineList.get(len - 1).getTimelineId();
        // TODO DTO에 lastTimelineId보내주고, isLastPage = true 도 보내주고
        return list;
    }


}
