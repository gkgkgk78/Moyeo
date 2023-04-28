package com.moyeo.service;

import com.moyeo.conponent.AwsS3;
import com.moyeo.entity.Photo;
import com.moyeo.entity.Post;
import com.moyeo.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Log4j2
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
    private final AwsS3 awsS3;
    private final PhotoRepository photoRepository;

    // 각각의 imageFile을 Photo 객체로 저장 후 photo 객체들의 리스트를 반환 (이후, Post 속성 값 중 하나인 photoList에서 사용)
    @Override
    public List<Photo> createPhotoList(List<MultipartFile> imageFiles, Post savedPost) throws Exception {
        log.info("Starting createPhotoList transaction");
        List<Photo> photoList = new ArrayList<>();

        for (MultipartFile imageFile : imageFiles) {
            Photo savedPhoto = this.savePhoto(imageFile, savedPost);
            photoList.add(savedPhoto);
        }
        log.info("createPhotoList Transaction complete");
        return photoList;
    }

    @Override
    public List<Photo> createPhotoListTest(Post savedPost) throws Exception {
        log.info("Starting createPhotoList transaction");
        List<Photo> photoList = new ArrayList<>();

        for(int i=0;i<7;i++) {
            String uuid = UUID.randomUUID().toString();
            Photo savedPhoto =  Photo.builder()
                    .postId(savedPost)
                    .photoUrl(uuid)
                    .build();
            photoRepository.save(savedPhoto);
            photoList.add(savedPhoto);
        }
        log.info("createPhotoList Transaction complete");
        return photoList;
    }


    private Photo savePhoto(MultipartFile imageFile, Post savedPost) throws Exception {
        // imageFile S3에 올리고 imageURL 가져오기
        String photoUrl = awsS3.upload(imageFile, "Moyeo/Post");

        // photo 객체 생성
        log.info("Starting savePhoto transaction");
        Photo photo = Photo.builder()
                .postId(savedPost)
                .photoUrl(photoUrl)
                .build();
        photoRepository.save(photo);
        log.info("savePhoto Transaction complete");
        return photo;
    }

    
}
