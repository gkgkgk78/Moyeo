package com.moyeo.service;

import com.moyeo.entity.Photo;
import com.moyeo.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoService {
    List<Photo> createPhotoList(List<MultipartFile> imageFiles, Post savedPost) throws Exception;



    public List<Photo> createPhotoListTest(Post savedPost)throws Exception;
}
