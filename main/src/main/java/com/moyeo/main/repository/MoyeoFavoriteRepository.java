package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
