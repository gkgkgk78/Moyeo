package com.moyeo.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moyeo.main.entity.MoyeoFavorite;
import com.moyeo.main.id.MoyeoFavoriteID;

@Repository
public interface MoyeoFavoriteRepository extends JpaRepository<MoyeoFavorite, MoyeoFavoriteID> {
}
