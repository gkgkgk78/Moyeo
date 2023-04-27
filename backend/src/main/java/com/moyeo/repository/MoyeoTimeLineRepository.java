package com.moyeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.entity.MoyeoPost;
import com.moyeo.entity.MoyeoTimeLine;

@Repository
public interface MoyeoTimeLineRepository extends JpaRepository<MoyeoTimeLine, Long> {
}
