package com.moyeo.controller;
import com.moyeo.config.security.JwtTokenProvider;
import com.moyeo.dto.TokenRes;
import com.moyeo.dto.UserLoginReq;
import com.moyeo.dto.UserInfoRes;
import com.moyeo.dto.UserReq;
import com.moyeo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyeo.service.UserService;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RequestMapping("/api")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;
    public final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider){
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 유저 조회
    @GetMapping("/auth/user")
    public ResponseEntity<?> searchUserByNickname(@RequestParam("search") String search){
        List<UserInfoRes> resultList = userService.searchUserByNickname(search);
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    // 닉네임, 프로필 이미지 조회
    @GetMapping("/auth/user/info")
    public ResponseEntity<?> getNicknameAndProfileImage() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.getPrincipal() != null){
            User user = (User)auth.getPrincipal();
            UserInfoRes result = userService.getNicknameAndProfileImage(user.getUserUid());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 소셜 로그인(카카오)
    @PostMapping("/login/kakao")
    public ResponseEntity<?> signUpKakao(@RequestBody UserLoginReq userLoginReq) throws Exception {
        TokenRes tokenRes = userService.signUpKakao(userLoginReq);
        return new ResponseEntity<>(tokenRes, HttpStatus.OK);
    }

    // 회원 정보 수정 (프로필 이미지)
    @PostMapping(value = "/auth/user/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateUser(@RequestPart(required = false) MultipartFile profileImage, @RequestPart String nickname) throws Exception {
        log.info("profileImage, {}", profileImage);
        log.info("nickname: {}", nickname);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() != null) {
            User user = (User) auth.getPrincipal();
            UserInfoRes userInfoRes = userService.updateUserInfo(user.getUserUid(), profileImage, nickname);
            return new ResponseEntity<>(userInfoRes, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // accessToken 재발급
    @PostMapping("/login/reissuance")
    public ResponseEntity<?> reissuance(@RequestBody UserReq user, HttpServletResponse response){
        return ResponseEntity.ok().body("accessToken 갱신 완료");
    }
}
