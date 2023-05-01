package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.id.MoyeoMembersID;

@Repository
public interface MoyeoMembersRepository extends JpaRepository<MoyeoMembers, MoyeoMembersID> {
}
