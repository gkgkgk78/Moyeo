package com.moyeo.main.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.moyeo.main.dto.RegistMoyeoRes;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.repository.MoyeoTimeLineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j

public class MoyeoTimeLineServiceImpl implements MoyeoTimeLineService {
    private final MoyeoMembersServiceImpl moyeoMembersService;
    private final MoyeoTimeLineRepository moyeoTimeLineRepository;

    @Override
    @Transactional
    public RegistMoyeoRes registMoyeoTimeline(User user) throws BaseException {
        // 1. 참여 자격 체크하기: 여행 중이어야 하고, 다른 동행에 참여 중이면 안된다. (여행 중인 타임라인 리턴)
        TimeLine timeLine = moyeoMembersService.checkJoinable(user);

        // 2. 모여_타임라인_등록
        MoyeoTimeLine moyeoTimeLine = moyeoTimeLineRepository.save(new MoyeoTimeLine());

        // 3. 타임라인_and_모여_타임라인 등록
        // 4. 모여_멤버s 등록
        // 5. 멤버수_카운트 + 1
        return moyeoMembersService.joinMember(timeLine, moyeoTimeLine, user);
    }

}
