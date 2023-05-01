package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoPhoto;

@Repository
public interface MoyeoPhotoRepository extends JpaRepository<MoyeoPhoto, Long> {
}
