package com.danim.service;

import com.danim.dto.AddPostReq;
import com.danim.entity.Photo;
import com.danim.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoService {
    List<Photo> createPhotoList(List<MultipartFile> imageFiles, Post savedPost) throws Exception;



    public List<Photo> createPhotoListTest(Post savedPost)throws Exception;
}
