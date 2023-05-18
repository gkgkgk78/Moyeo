package com.moyeo.main.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.GetPostRes;
import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;

public interface MoyeoPostService {
    // 포스트 등록
    MoyeoPost createPost(AddPostReq addPostReq) throws Exception;
    // MoyeoPost insertPost(MoyeoPost savedPost, List<MoyeoPhoto> photoList, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception;
    MoyeoPost insertPost(List<MultipartFile> imageFiles, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception;
    void updateMoyeoPost(User user, Long postId) throws Exception;
    void deleteMoyeoPost(User user, Long postId) throws Exception;
}
