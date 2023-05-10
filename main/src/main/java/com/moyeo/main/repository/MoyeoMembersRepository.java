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
	Optional<MoyeoMembers> findFirstByUserIdAndMoyeoTimelineIdAndFinishTimeOrderByMoyeoMembersIdDesc(User user, Long moyeoTimelineId, LocalDateTime finishTime);
	Optional<MoyeoMembers> findFirstByUserIdOrderByMoyeoMembersIdDesc(User user);
	Optional<List<MoyeoMembers>> findAllByMoyeoTimelineIdAndFinishTime(Long moyeoTimelineId, LocalDateTime finishTime);
	List<MoyeoMembers> findAllByMoyeoTimelineId(Long moyeoTimelineId);

	// 동행에 참여 중인지 여부. (userID와 finishTime이 null인 것을 찾았다면 동행 참여 중)
	Optional<MoyeoMembers> findFirstByUserIdAndFinishTime(User user, LocalDateTime finishTime);

}
