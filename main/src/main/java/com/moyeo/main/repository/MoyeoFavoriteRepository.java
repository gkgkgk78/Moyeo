package com.moyeo.main.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.Favorite;
import com.moyeo.main.entity.MoyeoFavorite;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.id.MoyeoFavoriteID;

@Repository
public interface MoyeoFavoriteRepository extends JpaRepository<MoyeoFavorite, MoyeoFavoriteID> {
	// 해당 포스트에 해당 유저가 좋아요 눌렀는지 여부 검색
	MoyeoFavorite findFirstByMoyeoPostIdAndUserId(MoyeoPost moyeoPost, User user);

	Optional<List<MoyeoFavorite>> findAllByUserId(User user);

	@Query(nativeQuery = true, value = "select moyeo_post_id from moyeo_favorite where user_id = :userId")
	Optional<List<Long>> findAllMoyeoPostIdByUserId(Long userId);
}
