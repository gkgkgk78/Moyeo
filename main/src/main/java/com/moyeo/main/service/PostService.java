package com.moyeo.main.service;

import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.BasePostDto;
import com.moyeo.main.dto.GetPostRes;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    // 포스트 등록
    Post createPost(AddPostReq addPostReq) throws Exception;
    Post insertPost(List<MultipartFile> imageFiles, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception;

    // 포스트 삭제
    void deletePostById(Long postId) throws Exception;

    // 지역명 검색어에 따른 post 조회
    List<GetPostRes> findByLocation(String location) throws Exception;

    Post insertPostTest(Post savedPost, List<Photo> photoList,AddPostReq addPostReq) throws Exception;
    List<GetPostRes> findMyPost(String location, Long userUid) throws Exception;

}
