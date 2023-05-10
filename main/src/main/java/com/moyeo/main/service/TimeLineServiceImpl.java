package com.moyeo.main.service;

import com.moyeo.main.dto.BasePostDto;
import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.MyPostDtoRes;
import com.moyeo.main.dto.PostMembers;
import com.moyeo.main.dto.TimelinePostInner;
import com.moyeo.main.dto.TimelinePostOuter;
import com.moyeo.main.entity.*;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.entity.Favorite;
import com.moyeo.main.entity.Nation;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;
import com.moyeo.main.repository.FavoriteRepository;
import com.moyeo.main.repository.NationRepository;
import com.moyeo.main.repository.PhotoRepository;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.TimeLineRepository;
import com.moyeo.main.repository.UserRepository;
import com.moyeo.main.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class TimeLineServiceImpl implements TimeLineService {
    private final TimeLineRepository timeLineRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FavoriteRepository favoriteRepository;
    private final UtilService utilService;
    // private final TimeLineRedisRepository repo;
    private final PostService postService;
    private final MoyeoMembersRepository moyeoMembersRepository;
    private final TimeLineAndMoyeoRepository timeLineAndMoyeoRepository;
    private final MoyeoPostRepository moyeoPostRepository;
    private final MoyeoPublicRepository moyeoPublicRepository;
    private final MoyeoFavoriteRepository moyeoFavoriteRepository;


    @Override
    //모든 최신 타임라인 얻어옴 , 페이징 x
    public List<TimeLine> searchTimelineOrderBylatest() throws BaseException {
        List<TimeLine> timeline = timeLineRepository.findAll(Sort.by(Sort.Direction.DESC, "createTime"));
        return timeline;
    }

    @Override
    //나의 타임라인 얻어옴 , 페이징 x
    public List<TimeLine> searchMyTimeline(Long uid, User now) throws BaseException {
        List<TimeLine> timeline = timeLineRepository.findAllByUserId(now).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        return timeline;
    }

    //찾고자 하는 상대 유저의 public이지 않은 타임라인 탐색 하는 메서드
    @Override
    public List<TimeLine> searchTimelineNotPublic(Long uid) throws BaseException {
        User now = userRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        List<TimeLine> timeline = timeLineRepository.findAllByUserIdAndIsTimelinePublic(now, true).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        return timeline;
    }

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

    @Override
    public Long makenewTimeline(User now) throws BaseException {

        // 이미 여행 중인지 체크 TODO
        log.info("유저ID: {}", now.getUserId());
        Optional<TimeLine> travelTimeLine = timeLineRepository.findFirstByUserIdAndIsComplete(now, false);
        if(travelTimeLine.isPresent()) {
            new BaseException(ErrorMessage.ALREADY_TRAVELING);
        }

        //여기서 넘어온 uid는 User의 uid아이디 입니다.
        TimeLine timeline = new TimeLine();

        //새로운 타임라인 생성이 가능한다
        timeline.setUserId(now);
        timeLineRepository.save(timeline);
        Long u1 = timeLineRepository.findLastTimelineId();
        //System.out.println(u1);
        return u1;
    }


    @Override
    public void makenewTimelineTemp() throws BaseException {
        //여기서 넘어온 uid는 User의 uid아이디 입니다.
        TimeLine timeline = new TimeLine();
        User now = userRepository.getByUserId(1L);
        //새로운 타임라인 생성이 가능한다
        timeline.setUserId(now);
        timeLineRepository.save(timeline);

    }

    @Override

    public void changeTimelineFinish() {
        timeLineRepository.changeTimeline(true);
    }


    @Override
    public void finishTimeline(Long uid, String title, User user) throws BaseException {
        TimeLine now = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        // 동행 중이라면 동행을 끝내고 타임라인을 종료할 수 있다.
        Optional<MoyeoMembers> optionalMembers = moyeoMembersRepository.findFirstByUserIdAndFinishTime(user.getUserId(), null);
        if (optionalMembers.isPresent()) {
            // 이미 동행중
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
    public void deleteTimeline(Long uid, User user) throws Exception {
        TimeLine now = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        if (!now.getUserId().getUserId().equals(user.getUserId()))
            throw new BaseException(ErrorMessage.NOT_PERMIT_USER);
        List<Post> post_list = postRepository.findAllByTimelineId(now);
        for (Post p : post_list) {
            postService.deletePostById(p.getPostId());
        }

        timeLineRepository.delete(now);
        // repo.deleteAll();
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
        // repo.deleteAll();
        return check;

    }

    @Override
    public List<MainTimelinePhotoDtoRes> getTimelineList(Long userId, Boolean isMine, Pageable pageable) {
        log.info("타임라인 목록 조회 서비스 시작...");

        Page<TimeLine> timeline = null;
        User user = null;

        if(userId == null) {
            // 메인 페이지에서 타임라인 목록 조회 -> 최신순
            timeline = timeLineRepository.findAllByIsCompleteAndIsTimelinePublic(true, true, pageable);
        } else {
            user = userRepository.findById(userId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

            if(isMine){
                // 마이 페이지에서 내 타임라인 목록 조회
                timeline = timeLineRepository.findAllByUserIdOrderByCreateTimeDesc(user, pageable);
            } else {
                // 다른 유저의 타임라인 목록 조회
                timeline = timeLineRepository.findAllByUserIdAndIsTimelinePublic(user, true, pageable);
            }
        }

        //이제 얻어낸 타임라인 리스트에 해당 되는 포스트 정보를 불러오도록 한다.
        List<MainTimelinePhotoDtoRes> list = new ArrayList<>();//넘겨줄 timeline dto생성

        if (timeline.getContent().size() == 0) {
            return list;
            //throw new BaseException(ErrorMessage.NOT_EXIST_TIMELINE_PAGING);
        }

        // if (pageable.getPageNumber() != 0 && timeline.getContent().size() == 0) {
        //     //throw new BaseException(ErrorMessage.NOT_EXIST_TIMELINE_PAGING);
        //     return list;
        // } else if (pageable.getPageNumber() == 0 && timeline.getContent().size() == 0) {
        //     return list;
        // }

        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);

            // moyeo post 가져오기
            // (1) timeline_and_moyeo 에서 timelineId로 moyeo_timeline_id 리스트 가져오기
            List<Long> moyeoTimelineIdList = timeLineAndMoyeoRepository.findAllMoyeoTimelineIdByTimlineId(time.getTimelineId()).orElse(null);
            MoyeoPost startMoyeoPost = null;
            MoyeoPost lastMoyeoPost = null;
            if(userId == null) {
                // 메인 페이지에서 타임라인 목록 조회 -> 최신순
                // (2-1) moyeo_post에서 moyeoTimelineIdList로 첫번째 moyeo post, 마지막 moyeo post 가져오기
                // (2-2) moyeo_post 조건: 모두가 공개로 설정 && 삭제되지 않은
                startMoyeoPost = moyeoPostRepository.findFirstVisiblePost(moyeoTimelineIdList);
               lastMoyeoPost = moyeoPostRepository.findLastVisiblePost(moyeoTimelineIdList);
            } else {
                List<Long> moyeoPostIdList = new ArrayList<>();
                if(isMine){
                    // 마이 페이지에서 내 타임라인 목록 조회
                    moyeoPostIdList = moyeoPublicRepository.getMyMoyeoPostIdList(userId, false).orElse(null);
                } else {
                    // 다른 유저의 타임라인 목록 조회
                    moyeoPostIdList = moyeoPublicRepository.getMoyeoPostIdList(userId, false, true).orElse(null);
                }
                startMoyeoPost = moyeoPostRepository.findFirstVisiblePostByUserId(moyeoTimelineIdList, moyeoPostIdList);
                lastMoyeoPost = moyeoPostRepository.findLastVisiblePostByUserId(moyeoTimelineIdList, moyeoPostIdList);
            }


            String thumbnailUrl = "";
            String startPlace = "";
            String lastPlace = "";

            if(startpost != null && (startMoyeoPost == null || startMoyeoPost != null && startpost.getCreateTime().isBefore(startMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 start!
                List<Photo> photoList = startpost.getPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startpost.getAddress2();
            } else if(startMoyeoPost != null && (startpost == null || startpost != null && startMoyeoPost.getCreateTime().isBefore(startpost.getCreateTime()))) {
                // 모여 포스트가 start!
                List<MoyeoPhoto> photoList = startMoyeoPost.getMoyeoPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startMoyeoPost.getAddress2();
            }

            if(lastpost != null && (lastMoyeoPost == null || lastMoyeoPost != null && lastpost.getCreateTime().isAfter(lastMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 last!
                lastPlace = lastpost.getAddress2();
            } else if(lastMoyeoPost != null && (lastpost == null || lastpost != null && lastMoyeoPost.getCreateTime().isAfter(lastpost.getCreateTime()))) {
                // 모여 포스트가 last!
                lastPlace = lastMoyeoPost.getAddress2();
            }

            if(userId == null) {
                // 메인 페이지에서 타임라인 목록 조회 -> 최신순
                user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
            }

            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(time, user, thumbnailUrl, startPlace, lastPlace).build());

        }

        log.info("타임라인 목록 조회 서비스 종료...");

        return list;
    }

    //타임라인 중에서 완료가 된 여행과 공개가 된 여행을 페이징 처리르 하여 보여준다 => 메인 피드 화면에서 타임라인과 썸네일 같이 넘어감
    @Override
    public List<MainTimelinePhotoDtoRes> searchTimelineOrderBylatestPaging(Pageable pageable) throws BaseException {
        log.info("test timelineservice 접근 !");
        Page<TimeLine> timeline = timeLineRepository.findAllByIsCompleteAndIsTimelinePublic(true, true, pageable);


        if (pageable.getPageNumber() != 0 && timeline.getContent().size() == 0) {
            //throw new BaseException(ErrorMessage.NOT_EXIST_TIMELINE_PAGING);
            return null;
        } else if (pageable.getPageNumber() == 0 && timeline.getContent().size() == 0) {
            return null;
        }

        //이제 얻어낸 타임라인 리스트에 해당 되는 포스트 정보를 불러오도록 한다.
        List<MainTimelinePhotoDtoRes> list = new ArrayList<>();//넘겨줄 timeline dto생성
        //이때 타임라인에서 post가 있는 친구는 보여주고 없으면 보여 주지 않아야 할듯 하다

        // for (TimeLine time : timeline) {
        //     Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
        //     Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);
        //     //지금 상태로는 타임라인에 등록이 된 post가 아닌지 확인을 해서 넘겨 주도록 해야한다
        //     if (startpost == null || lastpost == null)
        //         continue;
        //     //현재는 우선 임시로 작업을 하여 넣어 줄것으로 생각을 하고 있다.
        //     log.info("현재 timelineid" + time.getTimelineId() + "현재 post" + startpost.getPostId().toString());
        //     if (startpost.getPhotoList().isEmpty())
        //         throw new BaseException(ErrorMessage.NOT_EXIST_PHOTO);
        //     Photo photo = photoRepository.findById(startpost.getPhotoList().get(0).getPhotoId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_PHOTO));
        //     //Long uid = time.getTimelineId();
        //     User user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        //     MainTimelinePhotoDtoRes temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
        //     list.add(temp);
        // }
        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);

            // moyeo post 가져오기
            // (1) timeline_and_moyeo 에서 timelineId로 moyeo_timeline_id 리스트 가져오기
            List<Long> moyeoTimelineIdList = timeLineAndMoyeoRepository.findAllMoyeoTimelineIdByTimlineId(time.getTimelineId()).orElse(null);
            // (2-1) moyeo_post에서 moyeoTimelineIdList로 첫번째 moyeo post, 마지막 moyeo post 가져오기
            // (2-2) moyeo_post 조건: 모두가 공개로 설정 && 삭제되지 않은
            MoyeoPost startMoyeoPost = moyeoPostRepository.findFirstVisiblePost(moyeoTimelineIdList);
            MoyeoPost lastMoyeoPost = moyeoPostRepository.findLastVisiblePost(moyeoTimelineIdList);


            String thumbnailUrl = "";
            String startPlace = "";
            String lastPlace = "";

            if(startpost != null && (startMoyeoPost == null || startMoyeoPost != null && startpost.getCreateTime().isBefore(startMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 start!
                List<Photo> photoList = startpost.getPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startpost.getAddress2();
            } else if(startMoyeoPost != null && (startpost == null || startpost != null && startMoyeoPost.getCreateTime().isBefore(startpost.getCreateTime()))) {
                // 모여 포스트가 start!
                List<MoyeoPhoto> photoList = startMoyeoPost.getMoyeoPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startMoyeoPost.getAddress2();
            }

            if(lastpost != null && (lastMoyeoPost == null || lastMoyeoPost != null && lastpost.getCreateTime().isAfter(lastMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 last!
                lastPlace = lastpost.getAddress2();
            } else if(lastMoyeoPost != null && (lastpost == null || lastpost != null && lastMoyeoPost.getCreateTime().isAfter(lastpost.getCreateTime()))) {
                // 모여 포스트가 last!
                lastPlace = lastMoyeoPost.getAddress2();
            }

            User user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(time, user, thumbnailUrl, startPlace, lastPlace).build());

        }

        return list;
    }

    //나의 타임라인 검색시 페이징 처리해서 검색을 해온다 => 나의 타임라인 조회를 할시에 비어 있는 타임라인으로 넘겨줄거임
    @Override
    public List<MainTimelinePhotoDtoRes> searchMyTimelineWithPaging(User now, Pageable pageable) throws BaseException {
        User user = userRepository.findById(now.getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        Long myId = user.getUserId();

        Page<TimeLine> timeline = timeLineRepository.findAllByUserIdOrderByCreateTimeDesc(now, pageable);
        if (timeline.getContent().size() == 0) {
            return new ArrayList<>();
            //throw new BaseException(ErrorMessage.NOT_EXIST_TIMELINE_PAGING);
        }

        //이제 얻어낸 타임라인 리스트에 해당 되는 포스트 정보를 불러오도록 한다.
        List<MainTimelinePhotoDtoRes> list = new ArrayList<>();//넘겨줄 timeline dto생성
        //타임라인을 얻어옴, =>
        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);

            // moyeo post 가져오기
            // (1) timeline_and_moyeo 에서 timelineId로 moyeo_timeline_id 리스트 가져오기
            List<Long> moyeoTimelineIdList = timeLineAndMoyeoRepository.findAllMoyeoTimelineIdByTimlineId(time.getTimelineId()).orElse(null);
            // (2-1) moyeo_post에서 moyeoTimelineIdList로 첫번째 moyeo post, 마지막 moyeo post 가져오기
            // (2-2) moyeo_post 조건: 모두가 공개로 설정 && 삭제되지 않은
            List<Long> moyeoPostIdList = moyeoPublicRepository.getMyMoyeoPostIdList(myId, false).orElse(null);
            MoyeoPost startMoyeoPost = moyeoPostRepository.findFirstVisiblePostByUserId(moyeoTimelineIdList, moyeoPostIdList);
            MoyeoPost lastMoyeoPost = moyeoPostRepository.findLastVisiblePostByUserId(moyeoTimelineIdList, moyeoPostIdList);


            String thumbnailUrl = "";
            String startPlace = "";
            String lastPlace = "";

            if(startpost != null && (startMoyeoPost == null || startMoyeoPost != null && startpost.getCreateTime().isBefore(startMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 start!
                List<Photo> photoList = startpost.getPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startpost.getAddress2();
            } else if(startMoyeoPost != null && (startpost == null || startpost != null && startMoyeoPost.getCreateTime().isBefore(startpost.getCreateTime()))) {
                // 모여 포스트가 start!
                List<MoyeoPhoto> photoList = startMoyeoPost.getMoyeoPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startMoyeoPost.getAddress2();
            }

            if(lastpost != null && (lastMoyeoPost == null || lastMoyeoPost != null && lastpost.getCreateTime().isAfter(lastMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 last!
                lastPlace = lastpost.getAddress2();
            } else if(lastMoyeoPost != null && (lastpost == null || lastpost != null && lastMoyeoPost.getCreateTime().isAfter(lastpost.getCreateTime()))) {
                // 모여 포스트가 last!
                lastPlace = lastMoyeoPost.getAddress2();
            }

            // User user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(time, user, thumbnailUrl, startPlace, lastPlace).build());

        }
        return list;
    }

    //상대 타임라인 조회시 with Paging
    @Override
    public List<MainTimelinePhotoDtoRes> searchTimelineNotPublicWithPaging(Long uid, Pageable pageable) throws BaseException {
        User user = userRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

        Page<TimeLine> timeline = timeLineRepository.findAllByUserIdAndIsTimelinePublic(user, true, pageable);
        if (timeline.getContent().size() == 0) {
            return new ArrayList<>();
            //throw new BaseException(ErrorMessage.NOT_EXIST_TIMELINE);
        }
        Photo photo = null;
        Post post = null;
        MainTimelinePhotoDtoRes temp = null;
        if (timeline.getContent().size() == 0) {
            return new ArrayList<>();
            //throw new BaseException(ErrorMessage.NOT_EXIST_TIMELINE_PAGING);
        }
        //이제 얻어낸 타임라인 리스트에 해당 되는 포스트 정보를 불러오도록 한다.
        List<MainTimelinePhotoDtoRes> list = new ArrayList<>();//넘겨줄 timeline dto생성
        //타임라인을 얻어옴, =>
        // for (TimeLine time : timeline) {
        //     Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
        //     Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);
        //     //지금 상태로는 타임라인에 등록이 된 post가 아닌지 확인을 해서 넘겨 주도록 해야한다
        //     if (startpost == null || lastpost == null) {//해당 되는 부분에는
        //         photo = new Photo();
        //         photo.setPhotoUrl("");
        //         if (startpost == null) {
        //             startpost = new Post();
        //             startpost.setAddress2("");
        //         }
        //         if (lastpost == null) {
        //             lastpost = new Post();
        //             lastpost.setAddress2("");
        //         }
        //
        //         temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
        //         list.add(temp);
        //     } else {
        //         //현재는 우선 임시로 작업을 하여 넣어 줄것으로 생각을 하고 있다.
        //         photo = photoRepository.findById(startpost.getPhotoList().get(0).getPhotoId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        //         temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
        //         list.add(temp);
        //     }
        // }
        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);

            // moyeo post 가져오기
            // (1) timeline_and_moyeo 에서 timelineId로 moyeo_timeline_id 리스트 가져오기
            List<Long> moyeoTimelineIdList = timeLineAndMoyeoRepository.findAllMoyeoTimelineIdByTimlineId(time.getTimelineId()).orElse(null);
            // (2-1) moyeo_post에서 moyeoTimelineIdList로 첫번째 moyeo post, 마지막 moyeo post 가져오기
            // (2-2) moyeo_post 조건: 모두가 공개로 설정 && 삭제되지 않은
            List<Long> moyeoPostIdList = moyeoPublicRepository.getMoyeoPostIdList(uid, false, true).orElse(null);
            MoyeoPost startMoyeoPost = moyeoPostRepository.findFirstVisiblePostByUserId(moyeoTimelineIdList, moyeoPostIdList);
            MoyeoPost lastMoyeoPost = moyeoPostRepository.findLastVisiblePostByUserId(moyeoTimelineIdList, moyeoPostIdList);


            String thumbnailUrl = "";
            String startPlace = "";
            String lastPlace = "";

            if(startpost != null && (startMoyeoPost == null || startMoyeoPost != null && startpost.getCreateTime().isBefore(startMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 start!
                List<Photo> photoList = startpost.getPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startpost.getAddress2();
            } else if(startMoyeoPost != null && (startpost == null || startpost != null && startMoyeoPost.getCreateTime().isBefore(startpost.getCreateTime()))) {
                // 모여 포스트가 start!
                List<MoyeoPhoto> photoList = startMoyeoPost.getMoyeoPhotoList();
                if(photoList != null && photoList.size() != 0) thumbnailUrl = photoList.get(0).getPhotoUrl();
                startPlace = startMoyeoPost.getAddress2();
            }

            if(lastpost != null && (lastMoyeoPost == null || lastMoyeoPost != null && lastpost.getCreateTime().isAfter(lastMoyeoPost.getCreateTime()))) {
                // 일반 포스트가 last!
                lastPlace = lastpost.getAddress2();
            } else if(lastMoyeoPost != null && (lastpost == null || lastpost != null && lastMoyeoPost.getCreateTime().isAfter(lastpost.getCreateTime()))) {
                // 모여 포스트가 last!
                lastPlace = lastMoyeoPost.getAddress2();
            }

            // User user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
            // responseDto 리스트에 추가
            list.add(MainTimelinePhotoDtoRes.builder(time, user, thumbnailUrl, startPlace, lastPlace).build());

        }
        return list;
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
        // repo.deleteAll();
        return timeLine;
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
            // 할 거: post 없어도 그냥 보여주기~~

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
        // 나중에 DTO에 lastTimelineId보내주고, isLastPage = true 도 보내주고
        return list;
    }

}
