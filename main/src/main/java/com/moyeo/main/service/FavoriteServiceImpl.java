package com.moyeo.main.service;

import com.moyeo.main.entity.Favorite;
import com.moyeo.main.id.FavoriteID;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.FavoriteRepository;
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
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    // 해당 유저가 포스트에 좋아요 누른 여부 확인하기 (좋아요 기능)
    @Override
    @Transactional
    public boolean isFavorite (Long postId, Long userUid) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
        User user = userRepository.findById(userUid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

        FavoriteID favoriteID = new FavoriteID(post.getPostId(), user.getUserId());
        // Favorite favorite = favoriteRepository.findFirstByPostIdAndUserUid(post, user);
        // 좋아요 누른 적 없는 경우 - Favorite 생성 및 저장
        // if (favorite == null) {
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
    };

    // 포스트에 눌린 좋아요 수 합계
    @Override
    public Long countFavorite (Long postId) throws Exception{
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
        // Long TotalFavoriteNum = favoriteRepository.countByPostId(post);
        Long TotalFavoriteNum = post.getFavoriteCount();
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
