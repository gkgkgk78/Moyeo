package com.moyeo.main.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.MyPostDtoRes;
import com.moyeo.main.dto.TimelinePostInner;
import com.moyeo.main.dto.TimelinePostOuter;
import com.moyeo.main.dto.RegistMoyeoRes;
import com.moyeo.main.entity.Favorite;
import com.moyeo.main.entity.MoyeoMembers;
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
import com.moyeo.main.repository.MoyeoMembersRepository;
import com.moyeo.main.repository.MoyeoTimeLineRepository;
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

public class MoyeoTimeLineServiceImpl implements MoyeoTimeLineService {

    private final TimeLineRepository timeLineRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PhotoRepository photoRepository;

    private final FavoriteRepository favoriteRepository;
    private final NationRepository nationRepository;

    private final UtilService utilService;
    // private final TimeLineRedisRepository repo;
    private final PostService postService;

    private final MoyeoTimeLineRepository moyeoTimeLineRepository;
    private final TimeLineAndMoyeoRepository timeLineAndMoyeoRepository;
    private final MoyeoMembersRepository moyeoMembersRepository;

    @Transactional
    @Override
    public RegistMoyeoRes registMoyeoTimeline(User user) throws BaseException {
        // TODO MoyeoMembers의 joinMember함수로 하기
        // moye_time_line, time_line_and_moyeo, moyeo_members
        // 1-1. 현재 여행중인 타임라인 가져오기
        TimeLine timeLine = timeLineRepository.findByUserIdAndIsComplete(user, false).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        log.info("현재 여행중인 타임라인: {}", timeLine.getTimelineId());
        // 1-2. 현재 다른 동행에 참가 중인지 확인
        Optional<MoyeoMembers> optionalMembers = moyeoMembersRepository.findFirstByUserIdOrderByMoyeoMembersIdDesc(user);
        if (optionalMembers.isPresent()) {
            MoyeoMembers moyeoMembers = optionalMembers.get();
            if (moyeoMembers.getFinishTime() == null) {
                // 이미 동행중
                throw new BaseException(ErrorMessage.ALREADY_MOYEO);
            }
        }

        // 2. 모여_타임라인_등록
        MoyeoTimeLine moyeoTimeLine = moyeoTimeLineRepository.save(new MoyeoTimeLine());
        Long moyeoTimelineId = moyeoTimeLine.getMoyeoTimelineId();

        // 3. 타임라인_and_모여_타임라인 등록
        timeLineAndMoyeoRepository.save(TimeLineAndMoyeo.builder()
            .timelineId(timeLine)
            .moyeoTimelineId(moyeoTimeLine)
            .lastPostOrderNumber(timeLine.getLastPost()).build());

        // 4. 모여_멤버s 등록
        moyeoMembersRepository.save(MoyeoMembers.builder()
            .userId(user)
            .moyeoTimelineId(moyeoTimelineId)
            .build());

        // 5. 멤버수_카운트 + 1
        log.info("[registMoyeoMembers] 2-2. 모여_타임라인 카운트 + 1");
        moyeoTimeLine.updateMembersCount(1);
        moyeoTimeLine.setTitle("동행중"); // 현재 여행중인 타임라인 제목 "동행중"으로 수정
        moyeoTimeLineRepository.save(moyeoTimeLine);

        log.info("동행 타임라인 생성 끝...");
        return RegistMoyeoRes.builder().timelineId(timeLine.getTimelineId()).moyeoTimelineId(moyeoTimelineId).userId(user.getUserId()).build();
    }

}
