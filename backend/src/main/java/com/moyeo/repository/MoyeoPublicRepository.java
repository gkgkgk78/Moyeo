package com.moyeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.entity.MoyeoPost;
import com.moyeo.entity.MoyeoPublic;
import com.moyeo.id.MoyeoPublicID;

@Repository
public interface MoyeoPublicRepository extends JpaRepository<MoyeoPublic, MoyeoPublicID> {
}
