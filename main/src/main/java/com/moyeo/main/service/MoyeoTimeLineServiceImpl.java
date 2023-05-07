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
    private final MoyeoMembersServiceImpl moyeoMembersService;

    private final MoyeoTimeLineRepository moyeoTimeLineRepository;
    private final TimeLineAndMoyeoRepository timeLineAndMoyeoRepository;
    private final MoyeoMembersRepository moyeoMembersRepository;

    @Transactional
    @Override
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
