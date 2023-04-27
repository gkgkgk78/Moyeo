package com.moyeo.service;

import com.moyeo.dto.AddPostReq;
import com.moyeo.dto.GetPostRes;
import com.moyeo.entity.Photo;
import com.moyeo.entity.Post;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    // 포스트 등록
    Post createPost(AddPostReq addPostReq) throws Exception;
    Post makePost() throws Exception;
    Post insertPost(Post savedPost, List<Photo> photoList, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception;

    // 포스트 삭제
    void deletePostById(Long postId) throws Exception;

    // 지역명 검색어에 따른 post 조회
    List<GetPostRes> findByLocation(String location) throws Exception;

    Post insertPostTest(Post savedPost, List<Photo> photoList,AddPostReq addPostReq) throws Exception;
    List<GetPostRes> findMyPost(String location, Long userUid) throws Exception;
}
