package com.moyeo.main.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;

public interface MoyeoPhotoService {
    List<MoyeoPhoto> createPhotoList(List<MultipartFile> imageFiles, MoyeoPost savedPost) throws Exception;

}
