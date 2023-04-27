package com.moyeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.entity.MoyeoMembers;
import com.moyeo.entity.MoyeoPhoto;
import com.moyeo.id.MoyeoMembersID;

@Repository
public interface MoyeoPhotoRepository extends JpaRepository<MoyeoPhoto, Long> {
}
