package com.moyeo.main.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.moyeo.main.component.AwsS3;
import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.repository.MoyeoPhotoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class MoyeoPhotoServiceImpl implements MoyeoPhotoService {
    private final AwsS3 awsS3;
    private final MoyeoPhotoRepository moyeoPhotoRepository;

    @Override
    public List<MoyeoPhoto> createPhotoList(List<MultipartFile> imageFiles, MoyeoPost savedPost) throws Exception {
        log.info("Starting createPhotoList transaction");
        List<MoyeoPhoto> photoList = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            MoyeoPhoto savedPhoto = this.savePhoto(imageFile, savedPost);
            photoList.add(savedPhoto);
        }
        log.info("createPhotoList Transaction complete");
        return photoList;
    }

    private MoyeoPhoto savePhoto(MultipartFile imageFile, MoyeoPost savedPost) throws Exception {
        // imageFile S3에 올리고 imageURL 가져오기
        String photoUrl = awsS3.upload(imageFile, "Moyeo/Post");

        // photo 객체 생성
        log.info("Starting savePhoto transaction");
        MoyeoPhoto photo = MoyeoPhoto.builder()
                .moyeoPostId(savedPost)
                .photoUrl(photoUrl)
                .build();
        moyeoPhotoRepository.save(photo);
        log.info("savePhoto Transaction complete");
        return photo;
    }

}
