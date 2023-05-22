package com.moyeo.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.service.MoyeoPostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/api/auth/moyeo/post")
@RestController
@Slf4j
@Tag(name = "MoyeoPost", description = "모여 포스트 수정 및 삭제 기능")
public class MoyeoPostController {
    private final MoyeoPostService moyeoPostService;

    @PutMapping("/{moyeoPostId}")
    @Operation(summary = "모여 포스트 공개 여부 수정")
    public ResponseEntity<?> updateMoyeoPost(@PathVariable Long moyeoPostId) throws Exception {
        log.info("모여 포스트 공개 여부 수정 시작");
        if (moyeoPostId == null) {
            log.info("moyeoPostId 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();

        moyeoPostService.updateMoyeoPost(user, moyeoPostId);
        log.info("모여 포스트 공개 여부 수정 완료");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{moyeoPostId}")
    @Operation(summary = "모여 포스트 삭제", description = "is_deleted = true")
    public ResponseEntity<?> deleteMoyeoPost(@PathVariable Long moyeoPostId) throws Exception {
        log.info("모여 포스트 삭제 시작");
        if (moyeoPostId == null) {
            log.info("moyeoPostId 값 없음");
            throw new BaseException(ErrorMessage.VALIDATION_FAIL_EXCEPTION);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();

        moyeoPostService.deleteMoyeoPost(user, moyeoPostId);
        log.info("모여 포스트 삭제 완료");
        return ResponseEntity.ok().build();
    }

}
