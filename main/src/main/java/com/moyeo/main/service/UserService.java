package com.moyeo.main.service;

import com.moyeo.main.dto.TokenRes;
import com.moyeo.main.dto.UserLoginReq;
import com.moyeo.main.dto.UserInfoRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    // 유저 조회
    List<UserInfoRes> searchUserByNickname(String search);

    TokenRes signUpKakao(UserLoginReq userLoginReq) throws JsonProcessingException;

    UserInfoRes updateUserInfo(Long userUid, MultipartFile profileImage, String nickname) throws Exception;

    UserInfoRes getNicknameAndProfileImage(Long userUid) throws Exception;



    Boolean signUp();
}
