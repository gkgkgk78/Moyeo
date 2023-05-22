package com.moyeo.main.controller;

import com.moyeo.main.dto.ChangeFavoriteStatusRes;
import com.moyeo.main.dto.ChangeFavoriteStatusReq;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.service.FavoriteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Favorite", description = "좋아요 기능")
public class FavoriteController {
    private final FavoriteService favoriteService;

    // 해당 유저가 포스트에 좋아요를 누른 경우 -> favorite 저장 (true 반환)
    // 해당 유저가 포스트에 좋아요를 취소한 경우 -> favorite 삭제 (false 반환)
    @PostMapping(value = "")
    @Operation(summary = "좋아요 누르기/취소하기")
    public ResponseEntity<?> changeFavoriteStatus(@RequestBody @Valid ChangeFavoriteStatusReq changeFavoriteStatusReq
        , @RequestParam(required = false, defaultValue = "false") Boolean isMoyeo) throws Exception {
        // accessToken에서 userUid 값 가져오기
        Long userUid = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            userUid = user.getUserId();
        }

        // 응답
        ChangeFavoriteStatusRes res = new ChangeFavoriteStatusRes();
        res.setPostId(changeFavoriteStatusReq.getPostId());
        res.setMoyeo(isMoyeo);
        res.setFavorite(favoriteService.isFavorite(changeFavoriteStatusReq.getPostId(), userUid, isMoyeo));
        res.setTotalFavorite(favoriteService.countFavorite(changeFavoriteStatusReq.getPostId(), isMoyeo));

        return ResponseEntity.ok(res);
    }

    // 해당 유저가 좋아요 누른 포스트 검색
    @GetMapping(value = "")
    @Operation(summary = "해당 유저가 좋아요 누른 포스트 조회")
    public ResponseEntity<?> getFavoritePostList(@RequestParam Long userUid) throws Exception {
        if (userUid == null) {
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }

        return ResponseEntity.ok(favoriteService.findFavoritePost(userUid));
    }
}
