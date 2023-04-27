package com.moyeo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.entity.Favorite;
import com.moyeo.entity.MoyeoFavorite;
import com.moyeo.id.FavoriteID;
import com.moyeo.id.MoyeoFavoriteID;

@Repository
public interface MoyeoFavoriteRepository extends JpaRepository<MoyeoFavorite, MoyeoFavoriteID> {
}
