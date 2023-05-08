package com.moyeo.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.TimeLineAndMoyeo;

@Repository
public interface TimeLineAndMoyeoRepository extends JpaRepository<TimeLineAndMoyeo, Long> {
	List<TimeLineAndMoyeo> findAllByTimelineId(TimeLine timeLine);

	@Query(nativeQuery = true, value = "SELECT DISTINCT moyeo_timeline_id\n"
		+ "FROM time_line_and_moyeo\n"
		+ "WHERE timeline_id = :timelineId")
	Optional<List<Long>> findAllMoyeoTimelineIdByTimlineId(Long timelineId);
}
