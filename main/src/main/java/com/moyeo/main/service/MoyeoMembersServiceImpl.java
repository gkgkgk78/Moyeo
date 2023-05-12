package com.moyeo.main.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.moyeo.main.dto.MoyeoMembersReq;
import com.moyeo.main.dto.RegistMoyeoRes;
import com.moyeo.main.entity.MessageBox;
import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.TimeLineAndMoyeo;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.MessageBoxRepository;
import com.moyeo.main.repository.MoyeoMembersRepository;
import com.moyeo.main.repository.MoyeoTimeLineRepository;
import com.moyeo.main.repository.TimeLineAndMoyeoRepository;
import com.moyeo.main.repository.TimeLineRepository;
import com.moyeo.main.repository.UserRepository;

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
    private final MessageBoxRepository messageBoxRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Override
    public void inviteMoyeoMembers(User user, List<MoyeoMembersReq> moyeoMembersReqList) throws BaseException {

        for(MoyeoMembersReq moyeoMembersReq: moyeoMembersReqList) {

            // User invitee = userRepository.findById(moyeoMembersReq.getUserId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
            Optional<User> inviteeUser = userRepository.findById(moyeoMembersReq.getUserId());
            if(inviteeUser.isEmpty()) {
                log.info("userId {}는 존재하지 않는 유저 입니다.", moyeoMembersReq.getUserId());
                continue;
            }
            User invitee = inviteeUser.get();

            Long moyeoTimelineId = moyeoMembersReq.getMoyeoTimelineId();
            // MoyeoTimeLine moyeoTimeLine = moyeoTimeLineRepository.findById(moyeoTimelineId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_MOYEO_TIMELINE));
            if(moyeoTimeLineRepository.findById(moyeoTimelineId).isEmpty()) {
                log.info("moyeoTimelineId {}는 존재하지 않는 모여 타임라인입니다.", moyeoTimelineId);
                continue;
            }

            // log.info("(동행 초대 푸시 알림 보내기)");
            fcmService.send(user, invitee, moyeoTimelineId, "동행 초대 알림", "동행에 참여하시겠습니까?");

            // log.info("(메시지 함에 저장)");
            messageBoxRepository.save(MessageBox.builder()
                    .userId(invitee)
                    .content(user.getNickname() + "님이 동행에 초대하셨습니다.")
                    .createTime(LocalDateTime.now())
                    .inviteKey(moyeoTimelineId)
                    .build());
        }

        log.info("동행 초대 끝...");
    }

    @Override
    @Transactional
    public RegistMoyeoRes registMoyeoMembers(User user, Long moyeoTimelineId) throws BaseException {
        // 1. 참여 자격 체크하기: 여행 중이어야 하고, 다른 동행에 참여 중이면 안된다.
        TimeLine timeLine = checkJoinable(user); // (여행 중인 타임라인 리턴)

        MoyeoTimeLine moyeoTimeLine = moyeoTimeLineRepository.findById(moyeoTimelineId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        if(moyeoTimeLine.getIsComplete()) {
            throw new BaseException(ErrorMessage.ALREADY_DONE_MOYEO_TIMELINE);
        }

        return joinMember(timeLine, moyeoTimeLine, user);
    }

    @Override
    @Transactional
    public Boolean updateMoyeoMembers(User user, Long moyeoTimelineId) throws BaseException {
        MoyeoTimeLine moyeoTimeLine = moyeoTimeLineRepository.findById(moyeoTimelineId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        // moyeo_members에서 finish_time 기록
        MoyeoMembers moyeoMembers = moyeoMembersRepository.findFirstByUserIdAndMoyeoTimelineIdAndFinishTimeOrderByMoyeoMembersIdDesc(user, moyeoTimelineId, null).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        moyeoMembers.setFinishTime(LocalDateTime.now());
        moyeoMembersRepository.save(moyeoMembers);

        // 멤버가 1명이면 동행이 종료된다. moyeo_time_line에서 is_complete=1, finish_time
        if(moyeoTimeLine.getMembersCount() == 1) {
            moyeoTimeLine.setMembersCount(0L);
            moyeoTimeLine.setIsComplete(true);
            moyeoTimeLine.setFinishTime(LocalDateTime.now());
        } else {
            // moyeo_time_line에서 count - 1
            moyeoTimeLine.updateMembersCount(-1);
        }
        // moyeoTimeLine.setTitle("여행중"); // (X)
        moyeoTimeLineRepository.save(moyeoTimeLine);



        log.info("동행 나가기 끝...");
        return true;
    }

    public TimeLine checkJoinable(User user) {
        // 여행 중이어야 한다.
        log.info("여행 중인지 체크 중...");
        TimeLine timeLine = timeLineRepository.findFirstByUserIdAndIsComplete(user, false).orElseThrow(() -> new BaseException(ErrorMessage.NOT_TRAVELING));
        log.info("이미 동행 중인지 체크 중...");
        // 다른 동행에 참여 중이면 안된다.
        if(moyeoMembersRepository.findFirstByUserIdAndFinishTime(user, null).isPresent()) {
            throw new BaseException(ErrorMessage.ALREADY_MOYEO);
        }

        return timeLine;
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

        log.info("[registMoyeoMembers] 2-2. 모여_타임라인 멤버s_카운트 + 1");
        moyeoTimeLine.updateMembersCount(1);
        // moyeoTimeLine.setTitle("동행중"); // (X) // 현재 여행중인 타임라인 제목 "동행중"으로 수정
        moyeoTimeLineRepository.save(moyeoTimeLine);

        // timeLine.updateTitle("동행중"); // 현재 여행중인 타임라인 제목 "동행중"으로 수정

        log.info("동행 타임라인_생성/참여 끝...");
        return RegistMoyeoRes.builder()
            .timelineId(timeLine.getTimelineId())
            .moyeoTimelineId(moyeoTimelineId)
            .userId(user.getUserId()).build();
    }

}
