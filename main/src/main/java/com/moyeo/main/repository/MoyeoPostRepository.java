package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoPost;

@Repository
public interface MoyeoPostRepository extends JpaRepository<MoyeoPost, Long> {
}
