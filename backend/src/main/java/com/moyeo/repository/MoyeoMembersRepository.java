package com.moyeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.entity.MoyeoFavorite;
import com.moyeo.entity.MoyeoMembers;
import com.moyeo.id.MoyeoFavoriteID;
import com.moyeo.id.MoyeoMembersID;

@Repository
public interface MoyeoMembersRepository extends JpaRepository<MoyeoMembers, MoyeoMembersID> {
}
