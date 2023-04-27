package com.moyeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.entity.MoyeoPhoto;
import com.moyeo.entity.MoyeoPost;

@Repository
public interface MoyeoPostRepository extends JpaRepository<MoyeoPost, Long> {
}
