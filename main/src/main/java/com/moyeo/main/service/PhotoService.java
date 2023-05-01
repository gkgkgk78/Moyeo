package com.moyeo.main.service;

import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PhotoService {
    List<Photo> createPhotoList(List<MultipartFile> imageFiles, Post savedPost) throws Exception;



    public List<Photo> createPhotoListTest(Post savedPost)throws Exception;
}
