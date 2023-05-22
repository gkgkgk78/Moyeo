package com.moyeo.main.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.entity.User;

@Repository
public interface MoyeoMembersRepository extends JpaRepository<MoyeoMembers, Long> {
	Optional<MoyeoMembers> findFirstByUserIdAndMoyeoTimelineIdAndFinishTime(User user, Long moyeoTimelineId, LocalDateTime finishTime);

	// 현재 해당 모여 타임라인에 동행 중인 멤버들 (finishTime = null)
	Optional<List<MoyeoMembers>> findAllByMoyeoTimelineIdAndFinishTime(Long moyeoTimelineId, LocalDateTime finishTime);

	// 해당 유저가 동행 중인지 판단하기 위함. (finishTime이 null인 것을 찾았다면 해당 유저는 동행 중)
	Optional<MoyeoMembers> findFirstByUserIdAndFinishTime(User user, LocalDateTime finishTime);

}
