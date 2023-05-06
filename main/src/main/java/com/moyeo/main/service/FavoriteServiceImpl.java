package com.moyeo.main.service;

import com.moyeo.main.entity.Favorite;
import com.moyeo.main.entity.MoyeoFavorite;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.id.FavoriteID;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.id.MoyeoFavoriteID;
import com.moyeo.main.repository.FavoriteRepository;
import com.moyeo.main.repository.MoyeoFavoriteRepository;
import com.moyeo.main.repository.MoyeoPostRepository;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final PostRepository postRepository;
    private final MoyeoPostRepository moyeoPostRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final MoyeoFavoriteRepository moyeoFavoriteRepository;

    // 해당 유저가 포스트에 좋아요 누른 여부 확인하기 (좋아요 기능)
    @Override
    @Transactional
    public boolean isFavorite (Long postId, Long userUid, Boolean isMoyeo) throws Exception {
        User user = userRepository.findById(userUid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        if(isMoyeo == false) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
            FavoriteID favoriteID = new FavoriteID(post.getPostId(), user.getUserId());
            if (favoriteRepository.findById(favoriteID).isEmpty()) {
                Favorite newFavorite = new Favorite();
                newFavorite.setPostId(post);
                newFavorite.setUserId(user);
                favoriteRepository.save(newFavorite);

                post.updateFavoriteCount(1);

                return true;
            }
            // 좋아요 누른 적 있는 경우 - 해당 Favorite 삭제
            // favoriteRepository.deleteById(favorite.getFavoriteId());
            favoriteRepository.deleteById(favoriteID);
            post.updateFavoriteCount(-1);

            return false;
        } else {
            MoyeoPost post = moyeoPostRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_MOYEO_POST));
            MoyeoFavoriteID favoriteID = new MoyeoFavoriteID(post.getMoyeoPostId(), user.getUserId());
            if (moyeoFavoriteRepository.findById(favoriteID).isEmpty()) {
                MoyeoFavorite newFavorite = new MoyeoFavorite();
                newFavorite.setMoyeoPostId(post);
                newFavorite.setUserId(user);
                moyeoFavoriteRepository.save(newFavorite);

                post.updateFavoriteCount(1);

                return true;
            }
            // 좋아요 누른 적 있는 경우 - 해당 Favorite 삭제
            // favoriteRepository.deleteById(favorite.getFavoriteId());
            moyeoFavoriteRepository.deleteById(favoriteID);
            post.updateFavoriteCount(-1);

            return false;
        }

    };

    // 포스트에 눌린 좋아요 수 합계
    @Override
    public Long countFavorite (Long postId, Boolean isMoyeo) throws Exception{
        Long TotalFavoriteNum = 0L;

        if(isMoyeo == false) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
            TotalFavoriteNum = post.getFavoriteCount();
        } else {
            MoyeoPost post = moyeoPostRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_MOYEO_POST));
            TotalFavoriteNum = post.getFavoriteCount();
        }
        // Long TotalFavoriteNum = favoriteRepository.countByPostId(post);
        // Long TotalFavoriteNum = post.getFavoriteCount();
        return TotalFavoriteNum;
    }

    // 해당 유저가 좋아요 누른 포스트 검색
    @Override
    public List<Post> findFavoritePost (Long userUid) throws Exception {
        User user = userRepository.findById(userUid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER_FAV_POST));

        List<Post> favoritePostList = new ArrayList<>();
        for (Favorite favorite : favoriteList) {
            Post post = favorite.getPostId();
            favoritePostList.add(post);
        }
        return favoritePostList;
    }
}
