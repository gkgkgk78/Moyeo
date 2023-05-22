package com.moyeo.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moyeo.main.dto.MoyeoPostStatusDto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.entity.User;
import com.moyeo.main.id.MoyeoPublicID;

@Repository
public interface MoyeoPublicRepository extends JpaRepository<MoyeoPublic, MoyeoPublicID> {

	List<MoyeoPublic> findByMoyeoPostId(MoyeoPost moyeoPostId);

	@Query(nativeQuery = true, value = "SELECT SUM(is_deleted) > 0 AS isAnyDeleted, MIN(is_public) = 1 AS isAllPublic\n"
		+ "FROM moyeo_public\n"
		+ "WHERE moyeo_post_id = :moyeoPostId")
	MoyeoPostStatusDto getMoyeoPostStatus(Long moyeoPostId);

}
