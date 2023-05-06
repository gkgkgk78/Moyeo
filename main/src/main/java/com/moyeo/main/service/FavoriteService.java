package com.moyeo.main.service;

import com.moyeo.main.entity.Post;

import java.util.List;

public interface FavoriteService {
    boolean isFavorite (Long postId, Long userUid, Boolean isMoyeo) throws Exception;
    Long countFavorite (Long postId, Boolean isMoyeo) throws Exception;
    List<Post> findFavoritePost (Long userUid) throws Exception;
}