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

}
