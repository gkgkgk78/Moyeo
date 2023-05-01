package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.id.MoyeoPublicID;

@Repository
public interface MoyeoPublicRepository extends JpaRepository<MoyeoPublic, MoyeoPublicID> {
}
