package com.moyeo.controller;

import com.moyeo.dto.ChangeFavoriteStatusRes;
import com.moyeo.dto.ChangeFavoriteStatusReq;
import com.moyeo.entity.Post;
import com.moyeo.entity.User;
import com.moyeo.exception.BaseException;
import com.moyeo.exception.ErrorMessage;
import com.moyeo.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/auth/like")
@RestController
@Slf4j
public class FavoriteController {
    private final FavoriteService favoriteService;

    // 해당 유저가 포스트에 좋아요를 누른 경우 -> favorite 저장 (true 반환)
    // 해당 유저가 포스트에 좋아요를 취소한 경우 -> favorite 삭제 (false 반환)
    @PostMapping(value = "")
    public ResponseEntity<?> changeFavoriteStatus(@RequestBody @Valid ChangeFavoriteStatusReq changeFavoriteStatusReq) throws Exception {
        // accessToken에서 userUid 값 가져오기
        Long userUid = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            userUid = user.getUserUid();
        }

        // 응답
        ChangeFavoriteStatusRes res = new ChangeFavoriteStatusRes();
        res.setPostId(changeFavoriteStatusReq.getPostId());
        res.setFavorite(favoriteService.isFavorite(changeFavoriteStatusReq.getPostId(), userUid));
        res.setTotalFavorite(favoriteService.countFavorite(changeFavoriteStatusReq.getPostId()));
        return ResponseEntity.ok(res);
    }

    // 해당 유저가 좋아요 누른 포스트 검색
    @GetMapping(value = "")
    public ResponseEntity<?> getFavoritePostList(@RequestParam Long userUid) throws Exception {
        if (userUid == null) {
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }
        List<Post> favoritePostList = favoriteService.findFavoritePost(userUid);
        return ResponseEntity.ok(favoritePostList);
    }
}
