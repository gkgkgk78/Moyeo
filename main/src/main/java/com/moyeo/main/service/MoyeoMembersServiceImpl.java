package com.moyeo.main.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.moyeo.main.dto.RegistMoyeoRes;
import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.TimeLineAndMoyeo;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.MoyeoMembersRepository;
import com.moyeo.main.repository.MoyeoTimeLineRepository;
import com.moyeo.main.repository.TimeLineAndMoyeoRepository;
import com.moyeo.main.repository.TimeLineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class MoyeoMembersServiceImpl implements MoyeoMembersService {
    private final TimeLineRepository timeLineRepository;
    private final MoyeoMembersRepository moyeoMembersRepository;
    private final TimeLineAndMoyeoRepository timeLineAndMoyeoRepository;
    private final MoyeoTimeLineRepository moyeoTimeLineRepository;

    @Override
    public RegistMoyeoRes registMoyeoMembers(User user, Long moyeoTimelineId) throws BaseException {
        // 여행 중이어야 한다.
        TimeLine timeLine = timeLineRepository.findByUserIdAndIsComplete(user, false).orElseThrow(() -> new BaseException(ErrorMessage.NOT_TRAVELING));
        // 다른 동행에 참여 중이면 안된다.
        Optional<MoyeoMembers> optionalMembers = moyeoMembersRepository.findFirstByUserIdOrderByMoyeoMembersIdDesc(user);
        if (optionalMembers.isPresent()) {
            MoyeoMembers moyeoMembers = optionalMembers.get();
            if (moyeoMembers.getFinishTime() == null) {
                // 이미 동행중
                throw new BaseException(ErrorMessage.ALREADY_MOYEO);
            }
        }

        MoyeoTimeLine moyeoTimeLine = moyeoTimeLineRepository.findById(moyeoTimelineId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        return joinMember(timeLine, moyeoTimeLine, user);
    }

    @Override
    // @Transactional
    public RegistMoyeoRes updateMoyeoMembers(User user, Long moyeoTimelineId) throws BaseException {
        MoyeoTimeLine moyeoTimeLine = moyeoTimeLineRepository.findById(moyeoTimelineId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        log.info("동행 끝내기 기능");
        // moyeo_members에서 finish_time 기록
        MoyeoMembers moyeoMembers = moyeoMembersRepository.findFirstByUserIdAndMoyeoTimelineIdAndFinishTimeOrderByMoyeoMembersIdDesc(user, moyeoTimelineId, null).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        moyeoMembers.setFinishTime(LocalDateTime.now());
        moyeoMembersRepository.save(moyeoMembers);

        // 멤버가 1명이면 동행이 종료된다. moyeo_time_line에서 is_complete=1, finish_time
        if(moyeoTimeLine.getMembersCount() == 1) {
            moyeoTimeLine.setMembersCount(0L);
            moyeoTimeLine.setIsComplete(true);
            moyeoTimeLine.setFinishTime(LocalDateTime.now());
            moyeoTimeLine.setTitle("여행중");
            moyeoTimeLineRepository.save(moyeoTimeLine);
        } else {
            // moyeo_time_line에서 count - 1
            moyeoTimeLine.updateMembersCount(-1);
            moyeoTimeLine.setTitle("여행중");
            moyeoTimeLineRepository.save(moyeoTimeLine);
        }

        return null;
    }

    public RegistMoyeoRes joinMember(TimeLine timeLine, MoyeoTimeLine moyeoTimeLine, User user) {
        log.info("[registMoyeoMembers] 1. 타임라인_and_모여_타임라인 등록");
        timeLineAndMoyeoRepository.save(TimeLineAndMoyeo.builder()
            .timelineId(timeLine)
            .moyeoTimelineId(moyeoTimeLine)
            .lastPostOrderNumber(timeLine.getLastPost()).build());

        Long moyeoTimelineId = moyeoTimeLine.getMoyeoTimelineId();

        log.info("[registMoyeoMembers] 2-1. 모여_멤버s 등록");
        moyeoMembersRepository.save(MoyeoMembers.builder()
            .userId(user)
            .moyeoTimelineId(moyeoTimelineId)
            .build());

        log.info("[registMoyeoMembers] 2-2. 모여_타임라인 카운트 + 1 / timeline 제목 '동행중'");
        moyeoTimeLine.updateMembersCount(1);
        moyeoTimeLine.setTitle("동행중"); // 현재 여행중인 타임라인 제목 "동행중"으로 수정
        moyeoTimeLineRepository.save(moyeoTimeLine);

        return RegistMoyeoRes.builder()
            .timelineId(timeLine.getTimelineId())
            .moyeoTimelineId(moyeoTimelineId)
            .userId(user.getUserId()).build();
    }

}
