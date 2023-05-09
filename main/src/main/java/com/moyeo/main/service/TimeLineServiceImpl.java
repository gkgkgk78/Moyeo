package com.moyeo.main.service;

import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.MyPostDtoRes;
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

@Service
@RequiredArgsConstructor
@Slf4j

public class TimeLineServiceImpl implements TimeLineService {

    private final TimeLineRepository timeLineRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;

    private final FavoriteRepository favoriteRepository;
    private final NationRepository nationRepository;

    private final UtilService utilService;
    // private final TimeLineRedisRepository repo;
    private final PostService postService;

    private final MoyeoMembersRepository moyeoMembersRepository;


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

        //해당 되는 타임라인을 얻어 왔고
        TimeLine now = timeLineRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        //이제 그 다음으로 해당 되는 타임라인을 포스트를 얻어 올거임
        List<Post> post = postRepository.findAllByTimelineIdOrderByCreateTimeAsc(now);
        //이제 찾아 왔으므로 넘겨 줘야함


        //딕셔너리 형태로 해서 있으면 넣고 없으면 제외를 하도록 하자
        //타임 라인 하나를 넘겨 주는데 어떻게 넘겨 줄지 문제가 되네
        TimelinePostOuter timelineouter = new TimelinePostOuter();
        timelineouter.setIsComplete(now.getIsComplete());
        timelineouter.setIsPublic(now.getIsTimelinePublic());

        List<MyPostDtoRes> postlist = new ArrayList<>();


        TimelinePostInner temptimeline = new TimelinePostInner();

        Map<String, String> temp = new HashMap<String, String>();//그전에 국가 이름이 존재 하지 않는지 파악 하기 위해
        List<String> tempnow = new ArrayList<>();//여행한 국가의 모든 국가 리스트를 순서대로 겹치지 않게 파악하기 위해 해주는 작업
        List<String> photolist = new ArrayList<>();
        Long favorite_count = 0L;
        Favorite favorite_temp = null;
        Boolean favorite = false;
        Boolean isMine = false;
        Long nowUserUid = user.getUserId();
        int check = 0;
        Post last = null;
        for (Post p : post) {
            last = p;
            favorite_count = 0L;
            String NationName = p.getNationId().getName();
            isMine = false;
            // favorite_count = favoriteRepository.countByPostId(p);
            favorite_count = p.getFavoriteCount();
            favorite_temp = favoriteRepository.findFirstByPostIdAndUserId(p, user);

            if (favorite_temp == null)
                favorite = false;
            else favorite = true;

            TimeLine timelinetemp = p.getTimelineId();

            if (nowUserUid.equals(timelinetemp.getUserId().getUserId()))
                isMine = true;

            if (!temp.containsKey(NationName)) {//해당 부분은 여행 국가가 새로 나타난 형태를 의미를 함
                temp = new HashMap<String, String>();
                photolist = new ArrayList<>();
                temptimeline.setFinishDate(utilService.invertLocalDate(p.getCreateTime()));
                if (postlist.size() > 0) {
                    temptimeline.setPostList(postlist);
                    //그전에 했던 국가 , 국기, List<post>를 넣어주는 작업 진행할 부분
                    timelineouter.getTimeline().add(temptimeline);
                }

                for (Photo p1 : p.getPhotoList()) {
                    photolist.add(p1.getPhotoUrl());
                }

                //이제 새로운 타임라인 생성을 하고 국가, 국기, post를 넣어주는 작업이다
                postlist = new ArrayList<>();
                temptimeline = new TimelinePostInner();
                temptimeline.setStartDate(utilService.invertLocalDate(p.getCreateTime()));
                // temptimeline.setFlag(p.getNationUrl());

                Nation nation = p.getNationId();
                temptimeline.setFlag(nation.getNationUrl());

                temptimeline.setNation(NationName);
                //temptimeline.setIsMine(isMine);
                tempnow.add(NationName);
                temp.put(NationName, "1");

                postlist.add(MyPostDtoRes.builder(p, photolist, favorite_count, favorite).build());

            } else {
                photolist = new ArrayList<>();
                for (Photo p1 : p.getPhotoList()) {
                    photolist.add(p1.getPhotoUrl());
                }

                //나온 국가가 그전에 있던거에 이어져서 가는 형태로 파악을 하면됨
                postlist.add(MyPostDtoRes.builder(p, photolist, favorite_count, favorite).build());
            }
        }

        if (last != null) {
            temptimeline.setStartDate(utilService.invertLocalDate(now.getCreateTime()));
            temptimeline.setFinishDate(utilService.invertLocalDate(last.getCreateTime()));
        }
        else {
            temptimeline.setStartDate(utilService.invertLocalDate(now.getCreateTime()));
        }

        //가장 마지막에 남은 것들 처리해 주는 과정
        temptimeline.setPostList(postlist);
        timelineouter.getTimeline().add(temptimeline);

        timelineouter.setTitle(now.getTitle());
        if (post.size() == 0) {
            timelineouter.setTimeline(null);
            if (now.getUserId().getUserId().equals(user.getUserId()))
                isMine = true;
        }
        timelineouter.setIsMine(isMine);
        // timelineouter.setNationList(tempnow);//중복 되지 않는 타임라인의 모든 국가 리스트 를 설정해 주는 작업이다.
        return timelineouter;

    }

    @Override
    public Long makenewTimeline(User now) throws BaseException {

        // 이미 여행 중인지 체크
        timeLineRepository.findFirstByUserIdAndIsComplete(now, false).orElseThrow(() -> new BaseException(ErrorMessage.ALREADY_TRAVELING));

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
        Optional<MoyeoMembers> optionalMembers = moyeoMembersRepository.findByUserIdAndFinishTime(user.getUserId(), null);
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

    //타임라인 중에서 완료가 된 여행과 공개가 된 여행을 페이징 처리르 하여 보여준다 => 메인 피드 화면에서 타임라인과 썸네일 같이 넘어감
    @Override
    public List<MainTimelinePhotoDtoRes> searchTimelineOrderBylatestPaging(Pageable pageable) throws BaseException {
        log.info("test timelineservice 접근 !");
        // log.info("현재 검색 페이지 : {}", pageable.getPageNumber());
        // log.info("레지스 페이지 존재 여부 : {}", repo.findById(pageable.getPageNumber()).isPresent());
        // // redis에 존재할 시 바로 리턴
        // try {
        //     RedisPage entity = repo.findById(pageable.getPageNumber()).orElseThrow();
        //     log.info("redis 값 접근");
        //     return entity.getList();
        // } catch (NoSuchElementException e) {
        //     log.info("레디스 데이터 존재하지 않을 때 timeLineRepository 실행");
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

            for (TimeLine time : timeline) {
                Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
                Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);
                //지금 상태로는 타임라인에 등록이 된 post가 아닌지 확인을 해서 넘겨 주도록 해야한다
                if (startpost == null || lastpost == null)
                    continue;
                //현재는 우선 임시로 작업을 하여 넣어 줄것으로 생각을 하고 있다.
                log.info("현재 timelineid" + time.getTimelineId() + "현재 post" + startpost.getPostId().toString());
                if (startpost.getPhotoList().isEmpty())
                    throw new BaseException(ErrorMessage.NOT_EXIST_PHOTO);
                Photo photo = photoRepository.findById(startpost.getPhotoList().get(0).getPhotoId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_PHOTO));
                //Long uid = time.getTimelineId();
                User user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
                MainTimelinePhotoDtoRes temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
                list.add(temp);
            }
            // RedisPage redisPage = new RedisPage();
            // redisPage.setNum(pageable.getPageNumber());
            // redisPage.setList(list);
            // repo.save(redisPage);

            return list;
        // }
    }

    //나의 타임라인 검색시 페이징 처리해서 검색을 해온다 => 나의 타임라인 조회를 할시에 비어 있는 타임라인으로 넘겨줄거임
    @Override
    public List<MainTimelinePhotoDtoRes> searchMyTimelineWithPaging(User now, Pageable pageable) throws BaseException {
        // User now = userRepository.findById(uid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        Photo photo = null;
        User user = null;
        Post post = null;
        MainTimelinePhotoDtoRes temp = null;

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
            //지금 상태로는 타임라인에 등록이 된 post가 아닌지 확인을 해서 넘겨 주도록 해야한다
            if (startpost == null || lastpost == null) {//해당 되는 부분에는
                photo = new Photo();
                photo.setPhotoUrl("");
                if (startpost == null) {
                    startpost = new Post();
                    startpost.setAddress2("");
                }
                if (lastpost == null) {
                    lastpost = new Post();
                    lastpost.setAddress2("");
                }
                user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
                temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
                list.add(temp);
            } else {
                //현재는 우선 임시로 작업을 하여 넣어 줄것으로 생각을 하고 있다.
                photo = photoRepository.findById(startpost.getPhotoList().get(0).getPhotoId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_PHOTO));
                user = userRepository.findById(time.getUserId().getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
                temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
                list.add(temp);
            }
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
        for (TimeLine time : timeline) {
            Post startpost = postRepository.findTopByTimelineIdOrderByCreateTimeAsc(time);
            Post lastpost = postRepository.findTopByTimelineIdOrderByCreateTimeDesc(time);
            //지금 상태로는 타임라인에 등록이 된 post가 아닌지 확인을 해서 넘겨 주도록 해야한다
            if (startpost == null || lastpost == null) {//해당 되는 부분에는
                photo = new Photo();
                photo.setPhotoUrl("");
                if (startpost == null) {
                    startpost = new Post();
                    startpost.setAddress2("");
                }
                if (lastpost == null) {
                    lastpost = new Post();
                    lastpost.setAddress2("");
                }

                temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
                list.add(temp);
            } else {
                //현재는 우선 임시로 작업을 하여 넣어 줄것으로 생각을 하고 있다.
                photo = photoRepository.findById(startpost.getPhotoList().get(0).getPhotoId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
                temp = MainTimelinePhotoDtoRes.builder(time, startpost, lastpost, photo, user).build();
                list.add(temp);
            }
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

}
