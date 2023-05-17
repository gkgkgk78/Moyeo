package com.moyeo.main.service;

import java.time.LocalDateTime;
import java.util.List;

import com.moyeo.main.dto.PostInsertReq;
import com.moyeo.main.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.VoiceTextResult;
import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.Nation;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.id.MoyeoPublicID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoyeoPostServiceImpl implements MoyeoPostService {
    private final MoyeoPostRepository moyeoPostRepository;
    private final MoyeoTimeLineRepository moyeoTimeLineRepository;
    private final MoyeoPublicRepository moyeoPublicRepository;
    private final MoyeoMembersRepository moyeoMembersRepository;
    private final PostServiceImpl postService;
    private final MoyeoPhotoService moyeoPhotoService;

    private final UserRepository userRepository;

    private final AsyncTestService asyncTestService;


    // 포스트 생성 및 저장
    @Override
    public MoyeoPost createPost(AddPostReq addPostReq) throws Exception {
        MoyeoTimeLine moyeoTimeline = moyeoTimeLineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        if (moyeoTimeline.getIsComplete()) {
            throw new BaseException(ErrorMessage.ALREADY_DONE_MOYEO_TIMELINE);
        }

        MoyeoPost moyeoPost = new MoyeoPost();
        MoyeoPost savedPost = moyeoPostRepository.save(moyeoPost);
        return savedPost;
    }

    // 포스트 속성 값 설정 후 재저장
    @Override
    @Transactional
    public MoyeoPost insertPost(List<MultipartFile> imageFiles, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception {
    // public MoyeoPost insertPost(MoyeoPost savedPost, List<MoyeoPhoto> photoList, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception {
        // 모여타임라인에 소속된 멤버수(멤버즈카운트)가 1일 경우 post 작성이 불가능.
        MoyeoTimeLine moyeoTimeline = moyeoTimeLineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        if(moyeoTimeline.getMembersCount() != null && moyeoTimeline.getMembersCount() == 1) {
            throw new BaseException(ErrorMessage.NOT_ALLOWED_MOYEO_POST_REGISTRATION);
        }

        List<MoyeoMembers> moyeoMembers = moyeoMembersRepository.findAllByMoyeoTimelineIdAndFinishTime(addPostReq.getTimelineId(), null).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_MEMBERS));


        MoyeoPost savedPost = createPost(addPostReq);

        List<MoyeoPhoto> photoList = moyeoPhotoService.createPhotoList(imageFiles, savedPost);


        //파일 형식과 길이를 파악을 하여 post를 등록 시킬지 안시킬지 정하는 부분이다
        postService.checkVoiceFileValidity(voiceFile);

        // voiceFile 길이 추출, Clova로 text 변환, S3에 업로드
        VoiceTextResult voiceResult = postService.getVoiceFileLengthAndTextAndUpload(voiceFile);

        // db에 저장된 국가인 경우 가져와서 사용, 새로운 국가인 경우 nation 저장 후 사용
        Nation nation = postService.findNationOrSaveNation(addPostReq.getAddress1(), flagFile);

        // timeline 객체 가져오기
        // MoyeoTimeLine moyeoTimeline = moyeoTimeLineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        // TODO 확인
        LocalDateTime createTime = savedPost.getCreateTime();
        // moyeo_public 에 등록
        // List<MoyeoPublic> moyeoPublicList = new ArrayList<>();


        for (MoyeoMembers moyeoMember : moyeoMembers) {

            //해당 부분은 moyeopost insert 시 관련하여
            //푸시 알림을 위해 작업 하는 단계
            User temp_user = userRepository.getByUserId(moyeoMember.getUserId().getUserId());
            PostInsertReq req = PostInsertReq.builder(addPostReq, temp_user).build();
            asyncTestService.test(req);//해당 단계에서 비동기로 푸시 알람을 보낼 예정


            MoyeoPublic moyeoPublic = new MoyeoPublic();
            moyeoPublic.setUserId(moyeoMember.getUserId());
            moyeoPublic.setMoyeoPostId(savedPost);
            moyeoPublic.setCreateTime(createTime);
            moyeoPublicRepository.save(moyeoPublic);

            // moyeoPublicList.add(moyeoPublic);
        }


        // imageURL, voiceURL db에 저장하기
        log.info("Starting savePost transaction");
        savedPost.setMoyeoPhotoList(photoList);
        savedPost.setVoiceUrl(voiceResult.getVoiceUrl());
        savedPost.setVoiceLength(voiceResult.getDurationInSeconds());
        savedPost.setAddress1(addPostReq.getAddress1());
        savedPost.setAddress2(addPostReq.getAddress2());
        savedPost.setAddress3(addPostReq.getAddress3());
        savedPost.setAddress4(addPostReq.getAddress4());
        savedPost.setText(voiceResult.getText());
        savedPost.setMoyeoTimelineId(moyeoTimeline);
        savedPost.setNationId(nation);
        savedPost.setFavoriteCount(0L);
        // savedPost.setMoyeoPublicList(moyeoPublicList);
        MoyeoPost resavedPost = moyeoPostRepository.save(savedPost);
        log.info("savePost Transaction complete");


        return resavedPost;
    }

    @Override
    @Transactional
    public void updateMoyeoPost(User user, Long moyeoPostId) throws Exception {
        // 공개 여부 수정하기
        MoyeoPost moyeoPost = moyeoPostRepository.findById(moyeoPostId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
        MoyeoPublicID moyeoPublicID = new MoyeoPublicID(moyeoPostId, user.getUserId());
        MoyeoPublic moyeoPublic = moyeoPublicRepository.findById(moyeoPublicID).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_MOYEO_PUBLIC));
        moyeoPublic.updateIsPublic();
    }

    @Override
    @Transactional
    public void deleteMoyeoPost(User user, Long moyeoPostId) throws Exception {
        MoyeoPost moyeoPost = moyeoPostRepository.findById(moyeoPostId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));

        MoyeoPublicID moyeoPublicID = new MoyeoPublicID(moyeoPostId, user.getUserId());
        MoyeoPublic moyeoPublic = moyeoPublicRepository.findById(moyeoPublicID).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_MOYEO_PUBLIC));
        moyeoPublic.setIsDeleted(true);
    }

}
