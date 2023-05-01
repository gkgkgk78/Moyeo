package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.TimeLineAndMoyeo;

@Repository
public interface TimeLineAndMoyeoRepository extends JpaRepository<TimeLineAndMoyeo, Long> {
}
