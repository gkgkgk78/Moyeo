package com.moyeo.service;

import com.moyeo.entity.Favorite;
import com.moyeo.entity.FavoriteID;
import com.moyeo.entity.Post;
import com.moyeo.entity.User;
import com.moyeo.exception.BaseException;
import com.moyeo.exception.ErrorMessage;
import com.moyeo.repository.FavoriteRepository;
import com.moyeo.repository.PostRepository;
import com.moyeo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    // 해당 유저가 포스트에 좋아요 누른 여부 확인하기 (좋아요 기능)
    @Override
    public boolean isFavorite (Long postId, Long userUid) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
        User user = userRepository.findById(userUid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

        // (수정) 복합키로 변경됐기 때문에 삭제 다시 구현
        FavoriteID favoriteID = new FavoriteID();
        favoriteID.setPostId(post);
        favoriteID.setUserUid(user);

        // Favorite favorite = favoriteRepository.findFirstByPostIdAndUserUid(post, user);
        // 좋아요 누른 적 없는 경우 - Favorite 생성 및 저장
        // if (favorite == null) {
        if (favoriteRepository.findById(favoriteID).isEmpty()) {
            Favorite newFavorite = new Favorite();
            newFavorite.setPostId(post);
            newFavorite.setUserUid(user);
            favoriteRepository.save(newFavorite);
            return true;
        }
        // 좋아요 누른 적 있는 경우 - 해당 Favorite 삭제
        // favoriteRepository.deleteById(favorite.getFavoriteId());

        favoriteRepository.deleteById(favoriteID);

        return false;
    };

    // 포스트에 눌린 좋아요 수 합계
    @Override
    public Long countFavorite (Long postId) throws Exception{
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
        Long TotalFavoriteNum = favoriteRepository.countByPostId(post);
        return TotalFavoriteNum;
    }

    // 해당 유저가 좋아요 누른 포스트 검색
    @Override
    public List<Post> findFavoritePost (Long userUid) throws Exception {
        User user = userRepository.findById(userUid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));
        List<Favorite> favoriteList = favoriteRepository.findAllByUserUid(user).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER_FAV_POST));

        List<Post> favoritePostList = new ArrayList<>();
        for (Favorite favorite : favoriteList) {
            Post post = favorite.getPostId();
            favoritePostList.add(post);
        }
        return favoritePostList;
    }
}
