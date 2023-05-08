package com.moyeo.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.TimeLine;

@Repository
public interface MoyeoTimeLineRepository extends JpaRepository<MoyeoTimeLine, Long> {
	// Optional<List<MoyeoTimeLine>> findAllByTimelineId(TimeLine timeLine);
}
