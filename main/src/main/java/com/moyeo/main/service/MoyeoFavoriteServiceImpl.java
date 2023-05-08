package com.moyeo.main.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.moyeo.main.entity.Favorite;
import com.moyeo.main.entity.MoyeoFavorite;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.id.FavoriteID;
import com.moyeo.main.id.MoyeoFavoriteID;
import com.moyeo.main.repository.FavoriteRepository;
import com.moyeo.main.repository.MoyeoFavoriteRepository;
import com.moyeo.main.repository.MoyeoPostRepository;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoyeoFavoriteServiceImpl implements MoyeoFavoriteService {
    private final MoyeoPostRepository moyeoPostRepository;
    private final UserRepository userRepository;
    private final MoyeoFavoriteRepository moyeoFavoriteRepository;

    // 해당 유저가 포스트에 좋아요 누른 여부 확인하기 (좋아요 기능)
    @Override
    @Transactional
    public boolean isFavorite (Long postId, Long userUid, Boolean isMoyeo) throws Exception {
        MoyeoPost moyeoPost = moyeoPostRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
        User user = userRepository.findById(userUid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

        MoyeoFavoriteID moyeoFavoriteID = new MoyeoFavoriteID(moyeoPost.getMoyeoPostId(), user.getUserId());

        if (moyeoFavoriteRepository.findById(moyeoFavoriteID).isEmpty()) {
            MoyeoFavorite newFavorite = new MoyeoFavorite();
            newFavorite.setMoyeoPostId(moyeoPost);
            newFavorite.setUserId(user);
			moyeoFavoriteRepository.save(newFavorite);

			moyeoPost.updateFavoriteCount(1);

            return true;
        }
        // 좋아요 누른 적 있는 경우 - 해당 Favorite 삭제
		moyeoFavoriteRepository.deleteById(moyeoFavoriteID);
		moyeoPost.updateFavoriteCount(-1);

        return false;
    };

}
