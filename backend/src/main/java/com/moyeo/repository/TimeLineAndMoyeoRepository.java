package com.moyeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.entity.MoyeoTimeLine;
import com.moyeo.entity.TimeLineAndMoyeo;

@Repository
public interface TimeLineAndMoyeoRepository extends JpaRepository<TimeLineAndMoyeo, Long> {
}
