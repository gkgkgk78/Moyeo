package com.moyeo.main.service;

import com.moyeo.main.dto.BasePostDto;
import com.moyeo.main.dto.GetTimelineListRes;
import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.MyPostDtoRes;
import com.moyeo.main.dto.MemberInfoRes;
import com.moyeo.main.dto.ThumbnailAndPlace;
import com.moyeo.main.dto.TimelinePostInner;
import com.moyeo.main.dto.TimelinePostOuter;
import com.moyeo.main.entity.*;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;
import com.moyeo.main.repository.FavoriteRepository;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.TimeLineRepository;
import com.moyeo.main.repository.UserRepository;
import com.moyeo.main.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j

public class TimeLineServiceImpl implements TimeLineService {
    private final TimeLineRepository timeLineRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FavoriteRepository favoriteRepository;
    private final UtilService utilService;
    private final PostService postService;
    private final MoyeoMembersRepository moyeoMembersRepository;
    private final MoyeoPostRepository moyeoPostRepository;
    private final MoyeoPublicRepository moyeoPublicRepository;
    private final MoyeoFavoriteRepository moyeoFavoriteRepository;
    private final MoyeoMembersService moyeoMembersService;
    private final MoyeoPostService moyeoPostService;


    @Override
    public TimelinePostOuter searchOneTimeline(Long uid, User user) throws BaseException {

        TimeLine timeLine = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        User timelineUser = timeLine.getUserId();
        Boolean isMine = timelineUser.getUserId().equals(user.getUserId());

        // <일반 포스트 리스트 가져오기>
        List<Post> posts = postRepository.findAllByTimelineId(timeLine);
        List<BasePostDto> postList = new ArrayList<>();
        if(posts != null && posts.size() != 0) {
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

        // <모여 포스트 리스트도 가져오기!>
        List<MoyeoPost> moyeoPosts = new ArrayList<>();
        if(isMine) { // 내 타임라인을 조회하는 거라면 삭제된 포스트는 제외하고 가져와야 한다.
            moyeoPosts = moyeoPostRepository.findAllMoyeoPost(uid, timelineUser.getUserId());
        } else { // 타인의 타임라인을 조회하는 거라면 해당 타임라인 유저가 공개한 것만 가져와야 한다.
            moyeoPosts = moyeoPostRepository.findAllPublicMoyeoPost(uid, timelineUser.getUserId());
        }

        if(moyeoPosts == null || moyeoPosts.isEmpty()) {
            return addPostToResponse(timeLine, postList, isMine, timelineUser);
        }


        List<BasePostDto> moyeoPostList = new ArrayList<>();

        moyeoPostList = moyeoPosts.stream()
                .map(post -> {
                    Boolean isFavorite = false;
                    if(moyeoFavoriteRepository.findFirstByMoyeoPostIdAndUserId(post, user) != null) {
                        isFavorite = true;
                    }

                    return BasePostDto.builder(post
                            , isFavorite
                            , moyeoPublicRepository.findByMoyeoPostId(post).stream().map(MemberInfoRes::new).collect(Collectors.toList()))
                        .build();
                })
                .collect(Collectors.toList());

        // 합쳐서 createTime으로 정렬
        postList.addAll(moyeoPostList);
        Collections.sort(postList, Comparator.comparing(BasePostDto::getCreateTime));


        return addPostToResponse(timeLine, postList, isMine, timelineUser);
    }

    // 국가 별로 묶어서 보내주기
    public TimelinePostOuter addPostToResponse(TimeLine timeLine, List<BasePostDto> postList, Boolean isMine, User timelineUser) {
        // TimelinePostOuter
        TimelinePostOuter timelinePostOuter = new TimelinePostOuter();
        timelinePostOuter.setIsComplete(timeLine.getIsComplete());
        timelinePostOuter.setIsPublic(timeLine.getIsTimelinePublic());
        timelinePostOuter.setIsMine(isMine);
        timelinePostOuter.setTitle(timeLine.getTitle());

        // nowMoyeo: 이 타임라인으로 동행 중인지
        Boolean nowMoyeo = false;
        List<MemberInfoRes> nowMembers = new ArrayList<>();
        if(!timeLine.getIsComplete()) {

            Optional<MoyeoMembers> moyeoMembers = moyeoMembersRepository.findFirstByUserIdAndFinishTime(timelineUser, null);
            if(moyeoMembers.isPresent()){
                nowMoyeo = true; // 현재 해당 타임라인으로 동행 중
            }

            // nowMembers
            if(nowMoyeo) {
                Long moyeoTimelineId = moyeoMembers.get().getMoyeoTimelineId();
                nowMembers = moyeoMembersRepository.findAllByMoyeoTimelineIdAndFinishTime(moyeoTimelineId, null).orElse(null).stream()
                    .map(mem -> new MemberInfoRes(mem.getUserId()))
                    .collect(Collectors.toList());
            }
        }
        timelinePostOuter.setNowMoyeo(nowMoyeo);
        timelinePostOuter.setNowMembers(nowMembers);

        if(postList == null || postList.size() == 0) {
            return timelinePostOuter;
        }


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

    @Override
    public Long makenewTimeline(User now) throws BaseException {
        // 이미 여행 중인지 체크
        log.info("유저ID: {}", now.getUserId());
        if(timeLineRepository.findFirstByUserIdAndIsComplete(now, false).isPresent()) {
            throw new BaseException(ErrorMessage.ALREADY_TRAVELING);
        }

        //여기서 넘어온 uid는 User의 uid아이디 입니다.
        TimeLine timeline = new TimeLine();

        //새로운 타임라인 생성이 가능한다
        timeline.setUserId(now);
        timeLineRepository.save(timeline);
        Long u1 = timeLineRepository.findLastTimelineId();

        return u1;
    }

    @Override
    public void changeTimelineFinish() {
        timeLineRepository.changeTimeline(true);
    }


    @Override
    public void finishTimeline(Long uid, String title, User user) throws BaseException {
        TimeLine now = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        // 동행 중이라면 동행을 먼저 끝내야 한다.
        Optional<MoyeoMembers> optionalMembers = moyeoMembersRepository.findFirstByUserIdAndFinishTime(user, null);
        if (optionalMembers.isPresent()) { // 이미 동행중
            throw new BaseException(ErrorMessage.ALREADY_MOYEO);
        }

        //타임라인 완료 변경 작업 진행
        if (!now.getUserId().getUserId().equals(user.getUserId()))
            throw new BaseException(ErrorMessage.NOT_PERMIT_USER);
        now.setIsComplete(Boolean.TRUE);
        now.setFinishTime(LocalDateTime.now());
        now.setTitle(title);

        timeLineRepository.save(now);
    }


    @Override
    @Transactional
    public void deleteTimeline(Long uid, User user) throws Exception {
        TimeLine now = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        if (!now.getUserId().getUserId().equals(user.getUserId()))
            throw new BaseException(ErrorMessage.NOT_PERMIT_USER);

        // 동행 중이라면 동행을 먼저 끝내야 한다.
        Optional<MoyeoMembers> optionalMembers = moyeoMembersRepository.findFirstByUserIdAndFinishTime(user, null);
        if (optionalMembers.isPresent()) {
            // 이미 동행중이라면 동행 끝내기 기능 수행
            moyeoMembersService.updateMoyeoMembers(user, optionalMembers.get().getMoyeoTimelineId());
        }
        // 모여 포스트 is_deleted => true
        List<Long> moyeoPostIdList = moyeoPostRepository.findAllMoyeoPostIdByTimelineId(uid, user.getUserId());
        for(Long moyeoPostId : moyeoPostIdList) {
            moyeoPostService.deleteMoyeoPost(user, moyeoPostId);
        }


        List<Post> post_list = postRepository.findAllByTimelineId(now);
        for (Post p : post_list) {
            postService.deletePostById(p.getPostId());
        }

        timeLineRepository.delete(now);
    }

    @Override
    public Boolean changePublic(Long uid, User user) throws BaseException {
        TimeLine now = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        if (!now.getUserId().getUserId().equals(user.getUserId()))
            throw new BaseException(ErrorMessage.NOT_PERMIT_USER);

        Boolean temp = now.getIsTimelinePublic();
        Boolean check = false;
        //완료->비완료 , 비완료->완료 로 변경하는 작업
        if (temp) {
            now.setIsTimelinePublic(false);
            check = false;
        } else {
            now.setIsTimelinePublic(true);
            check = true;
        }
        timeLineRepository.save(now);

        return check;

    }


    // 메인 페이지에서 타임라인 목록 조회 -> 최신순
    @Override
    public List<MainTimelinePhotoDtoRes> getTimelineList(Pageable pageable) {
        Page<TimeLine> timeline = timeLineRepository.findAllByIsCompleteAndIsTimelinePublic(true, true, pageable);

        List<MainTimelinePhotoDtoRes> list = new ArrayList<>();//넘겨줄 timeline dto생성

        if (timeline.getContent().size() == 0) {
            return list;
        }

        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineId(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByPostIdDesc(time);

            // moyeo post 가져오기
            MoyeoPost startMoyeoPostForThumbnail = moyeoPostRepository.findFirstPublicMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());
            MoyeoPost startMoyeoPostForAddress = moyeoPostRepository.findFirstMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());
            MoyeoPost lastMoyeoPost = moyeoPostRepository.findLastMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());

            // 썸네일, 시작 장소, 마지막 장소 구하기
            ThumbnailAndPlace tumbnailAndPlace = getThumbnailAndPlace(startpost, lastpost, startMoyeoPostForThumbnail, startMoyeoPostForAddress, lastMoyeoPost, false);

            User user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(time, user, tumbnailAndPlace.getThumbnailUrl(), tumbnailAndPlace.getStartPlace(), tumbnailAndPlace.getLastPlace()).build());

        }

        return list;
    }

    public List<GetTimelineListRes> getTimelineList2() {
        // 메인 페이지에서 타임라인 목록 조회 -> 최신순
        // post 제일 첫번째 거 + 마지막 거

        List<GetTimelineListRes> publicTimelineList = timeLineRepository.getPublicTimelineList();

        for(GetTimelineListRes timelineRes : publicTimelineList) {

        }

        return timeLineRepository.getPublicTimelineList();

    }

    // 마이 페이지에서 타임라인 목록 조회 -> 최신순
    @Override
    public List<MainTimelinePhotoDtoRes> getTimelineList(User user, Pageable pageable) {
        Page<TimeLine> timeline = timeline = timeLineRepository.findAllByUserIdOrderByCreateTimeDesc(user, pageable);

        //이제 얻어낸 타임라인 리스트에 해당 되는 포스트 정보를 불러오도록 한다.
        List<MainTimelinePhotoDtoRes> list = new ArrayList<>();//넘겨줄 timeline dto생성

        if (timeline.getContent().size() == 0) {
            return list;
        }

        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineId(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByPostIdDesc(time);

            // moyeo post 가져오기
            MoyeoPost startMoyeoPost = moyeoPostRepository.findFirstMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());
            MoyeoPost lastMoyeoPost = moyeoPostRepository.findLastMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());

            // 썸네일, 시작 장소, 마지막 장소 구하기
            ThumbnailAndPlace tumbnailAndPlace = getThumbnailAndPlace(startpost, lastpost, null, startMoyeoPost, lastMoyeoPost, true);

            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(time, user, tumbnailAndPlace.getThumbnailUrl(), tumbnailAndPlace.getStartPlace(), tumbnailAndPlace.getLastPlace()).build());

        }

        return list;
    }

    // 다른 유저의 타임라인 목록 조회 -> 최신순
    @Override
    public List<MainTimelinePhotoDtoRes> getTimelineList(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

        Page<TimeLine> timeline = timeline = timeLineRepository.findAllByUserIdAndIsTimelinePublicAndIsComplete(user, true, true, pageable);

        //이제 얻어낸 타임라인 리스트에 해당 되는 포스트 정보를 불러오도록 한다.
        List<MainTimelinePhotoDtoRes> list = new ArrayList<>();//넘겨줄 timeline dto생성

        if (timeline.getContent().size() == 0) {
            return list;
        }

        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineId(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByPostIdDesc(time);

            // moyeo post 첫번째 & 마지막 가져오기
            MoyeoPost startMoyeoPostForThumbnail = moyeoPostRepository.findFirstPublicMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());
            MoyeoPost startMoyeoPostForAddress = moyeoPostRepository.findFirstMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());
            MoyeoPost lastMoyeoPost = moyeoPostRepository.findLastMoyeoPost(time.getTimelineId(), time.getUserId().getUserId());

            // 썸네일, 시작 장소, 마지막 장소 구하기
            ThumbnailAndPlace tumbnailAndPlace = getThumbnailAndPlace(startpost, lastpost, startMoyeoPostForThumbnail, startMoyeoPostForAddress, lastMoyeoPost, false);

            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(time, user, tumbnailAndPlace.getThumbnailUrl(), tumbnailAndPlace.getStartPlace(), tumbnailAndPlace.getLastPlace()).build());

        }

        return list;
    }

    public ThumbnailAndPlace getThumbnailAndPlace(Post startpost, Post lastpost, MoyeoPost startMoyeoPostForThumbnail, MoyeoPost startMoyeoPost, MoyeoPost lastMoyeoPost, Boolean isMyPage) {
        String thumbnailUrl = "";
        String startPlace = "";
        String lastPlace = "";

        if(startpost == null && startMoyeoPost == null) {
            return ThumbnailAndPlace.builder()
                .thumbnailUrl(thumbnailUrl)
                .startPlace(startPlace)
                .lastPlace(lastPlace).build();
        }

        if(!isMyPage) {
            if(startpost != null && (startMoyeoPostForThumbnail == null || startpost.getCreateTime().isBefore(startMoyeoPostForThumbnail.getCreateTime()))) {
                // 일반 포스트가 start
                List<Photo> photoList = startpost.getPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
            } else if(startMoyeoPostForThumbnail != null && (startpost == null || startMoyeoPostForThumbnail.getCreateTime().isBefore(startpost.getCreateTime()))) {
                // 모여 포스트가 start
                List<MoyeoPhoto> photoList = startMoyeoPostForThumbnail.getMoyeoPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
            }
            if(startpost != null && (startMoyeoPost == null || startpost.getCreateTime().isBefore(startMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 start
                startPlace = startpost.getAddress2();
            } else {
                // 모여 포스트가 start
                startPlace = startMoyeoPost.getAddress2();
            }
        } else {
            if(startpost != null && (startMoyeoPost == null || startpost.getCreateTime().isBefore(startMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 start
                List<Photo> photoList = startpost.getPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startpost.getAddress2();
            } else {
                // 모여 포스트가 start
                List<MoyeoPhoto> photoList = startMoyeoPost.getMoyeoPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startMoyeoPost.getAddress2();
            }
        }


        if(lastpost != null && (lastMoyeoPost == null || lastpost.getCreateTime().isAfter(lastMoyeoPost.getCreateTime()))) {
            // 일반 포스트가 last
            lastPlace = lastpost.getAddress2();
        } else {
            // 모여 포스트가 last
            lastPlace = lastMoyeoPost.getAddress2();
        }

        return ThumbnailAndPlace.builder()
            .thumbnailUrl(thumbnailUrl)
            .startPlace(startPlace)
            .lastPlace(lastPlace).build();

    }

    @Override
    public TimeLine isTraveling(Long uid) {
        User user = userRepository.getByUserId(uid);
        TimeLine timeLine;
        try {
            timeLine = timeLineRepository.findAllByUserIdAndIsComplete(user, false);
        } catch (Exception e) {
            return null;
        }

        return timeLine;
    }


}
