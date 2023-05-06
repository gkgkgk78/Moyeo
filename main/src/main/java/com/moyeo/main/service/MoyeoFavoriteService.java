package com.moyeo.main.service;

import java.util.List;

import com.moyeo.main.entity.Post;

public interface MoyeoFavoriteService {
    boolean isFavorite (Long postId, Long userUid, Boolean isMoyeo) throws Exception;

}