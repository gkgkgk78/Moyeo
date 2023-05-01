package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoTimeLine;

@Repository
public interface MoyeoTimeLineRepository extends JpaRepository<MoyeoTimeLine, Long> {
}
