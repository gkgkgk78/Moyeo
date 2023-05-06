package com.moyeo.main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.entity.User;
import com.moyeo.main.id.MoyeoPublicID;

@Repository
public interface MoyeoPublicRepository extends JpaRepository<MoyeoPublic, MoyeoPublicID> {
	@Modifying(clearAutomatically = true)
	@Query("UPDATE MoyeoPublic m set m.isDeleted= :deleted")
	void deleteMoyeoPost(Boolean deleted);

	MoyeoPublic findByUserIdAndMoyeoPostId(User user, MoyeoPost moyeoPostId);

	List<MoyeoPublic> findByMoyeoPostId(MoyeoPost moyeoPostId);
	List<MoyeoPublic> findByMoyeoPostId(Long moyeoPostId);
}
