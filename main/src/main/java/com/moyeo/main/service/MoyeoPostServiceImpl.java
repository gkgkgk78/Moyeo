package com.moyeo.main.service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyeo.main.conponent.AwsS3;
import com.moyeo.main.conponent.ClovaSpeechClient;
import com.moyeo.main.conponent.Http;
import com.moyeo.main.conponent.MultiFileToFile;
import com.moyeo.main.dto.AddPostReq;
import com.moyeo.main.dto.GetPostRes;
import com.moyeo.main.dto.WordInfo;
import com.moyeo.main.entity.Favorite;
import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.entity.MoyeoTimeLine;
import com.moyeo.main.entity.Nation;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.id.FavoriteID;
import com.moyeo.main.id.MoyeoPublicID;
import com.moyeo.main.repository.FavoriteRepository;
import com.moyeo.main.repository.MoyeoMembersRepository;
import com.moyeo.main.repository.MoyeoPhotoRepository;
import com.moyeo.main.repository.MoyeoPostRepository;
import com.moyeo.main.repository.MoyeoPublicRepository;
import com.moyeo.main.repository.MoyeoTimeLineRepository;
import com.moyeo.main.repository.NationRepository;
import com.moyeo.main.repository.PhotoRepository;
import com.moyeo.main.repository.PostRepository;
import com.moyeo.main.repository.TimeLineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoyeoPostServiceImpl implements MoyeoPostService {
    private final AwsS3 awsS3;
    private final MoyeoPostRepository moyeoPostRepository;
    private final MoyeoTimeLineRepository moyeoTimeLineRepository;
    private final MoyeoPublicRepository moyeoPublicRepository;
    private final MoyeoMembersRepository moyeoMembersRepository;
    private final NationRepository nationRepository;
    private final MultiFileToFile multiFileToFile;
    private final Http http;


    // 포스트 생성 및 저장
    @Override
    public MoyeoPost createPost(AddPostReq addPostReq) throws Exception {
        MoyeoTimeLine moyeoTimeline = moyeoTimeLineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        if (moyeoTimeline.getIsComplete() == true) {
            new BaseException(ErrorMessage.ALREADY_DONE_TIMELINE);
        }

        MoyeoPost moyeoPost = new MoyeoPost();
        MoyeoPost savedPost = moyeoPostRepository.save(moyeoPost);
        return savedPost;
    }

    // 포스트 속성 값 설정 후 재저장
    @Transactional
    @Override
    public MoyeoPost insertPost(MoyeoPost savedPost, List<MoyeoPhoto> photoList, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception {
        // 모여타임라인에 소속된 멤버수(멤버즈카운트)가 1일 경우 post 작성이 불가능.
        MoyeoTimeLine moyeoTimeline = moyeoTimeLineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        if(moyeoTimeline.getMembersCount() <= 1) {
            throw new BaseException(ErrorMessage.NOT_POST_EXCEPTION);
        }

        List<MoyeoMembers> moyeoMembers = moyeoMembersRepository.findAllByMoyeoTimelineIdAndFinishTime(addPostReq.getTimelineId(), null).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_MEMBERS));

        log.info("voiceFile : {}",voiceFile);
        //파일 형식과 길이를 파악을 하여 post를 등록 시킬지 안시킬지 정하는 부분이다
        String fileName = voiceFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!ext.equals("wav")) {
            System.out.println("웨이브 파일 아님");
            throw new BaseException(ErrorMessage.NOT_PERMIT_VOICE_SAVE);
        }

        //backend 폴더 하위에 temp라는 폴더를 생성, 즉 해당 되는 폴더가 없으면 임의로 생성을 한다는 것을 의미를 함
        Files.createDirectories(Paths.get("temp"));
        Path target = (Path) Paths.get("temp", voiceFile.getOriginalFilename());//파일이름으로 파일을 저장을 하고자 하며 , 경로를 의미
        try (InputStream inputStream = voiceFile.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);//파일 저장
        } catch (Exception e) {
            System.out.println("파일 임시파일에 저장 안 됨");
            throw new BaseException(ErrorMessage.NOT_PERMIT_VOICE_SAVE);
        }

        // 파일 길이 추출
        File file = new File(target.toUri());
        AudioFileFormat audioInputStream = AudioSystem.getAudioFileFormat(file);
        AudioFormat format = audioInputStream.getFormat();
        long frameLength = audioInputStream.getFrameLength();
        double durationInSeconds = (frameLength / format.getFrameRate());

        int check_time = (int) durationInSeconds;
        //System.out.println("Duration: " + durationInSeconds + " seconds");
        if (check_time > 30) {
            throw new BaseException(ErrorMessage.OVER_VOICE_TIME);
        }

        // voiceFile S3에 올리고 voiceURL 가져오기


        // voiceFile -> text 변환 : clova speech api 요청 보내기
        final ClovaSpeechClient clovaSpeechClient = new ClovaSpeechClient();
        ClovaSpeechClient.NestRequestEntity requestEntity = new ClovaSpeechClient.NestRequestEntity();
//        String result = clovaSpeechClient.url(voiceUrl, requestEntity);

        String result = clovaSpeechClient.upload(multiFileToFile.transTo(voiceFile),requestEntity);
        if (result.contains("\"result\":\"FAILED\"")) {
            new BaseException(ErrorMessage.NOT_STT_SAVE);
        }

        log.info("clova result: {}", result);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(result);
        String text = rootNode.get("text").asText();

        // List<WordInfo> fastApiReq = new ArrayList<>();
        //
        // for(JsonNode t :rootNode.get("segments")){
        //     for(int i = 0;i<t.get("words").size();i++){
        //         fastApiReq.add(WordInfo.builder()
        //                 .startTime(t.get("words").get(i).get(0).asLong())
        //                 .endTime(t.get("words").get(i).get(1).asLong())
        //                 .word((t.get("words").get(i).get(2).asText())).build());
        //     }
        // }
        // voiceFile -> text 변환 : 응답 결과 확인
        log.info("Clova info :{}",result);
        String voiceUrl="";
        voiceUrl = awsS3.upload(voiceFile, "Moyeo/Voice");
        Files.delete(target);//파일을 삭제하는 코드임
        log.info("voiceUrl info :{}",voiceUrl);
        // voiceFile -> text 변환 : 응답받은 json 파일에서 text 추출
        // voiceFile S3에 올리고 voiceURL 가져오기

        // db에 저장된 국가인 경우 가져와서 사용, 새로운 국가인 경우 nation 저장 후 사용
        String address1 = addPostReq.getAddress1();
        Nation nation = nationRepository.findFirstByName(address1);
        if (nation == null) {
            nation = new Nation();
            String flagUrl = awsS3.upload(flagFile, "Moyeo/Nation");
            nation.setNationUrl(flagUrl);
            nation.setName(address1);
            nationRepository.save(nation);
        }

        // timeline 객체 가져오기
        // MoyeoTimeLine moyeoTimeline = moyeoTimeLineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        // TODO 확인
        LocalDateTime createTime = savedPost.getCreateTime();
        // moyeo_public 에 등록
        // List<MoyeoPublic> moyeoPublicList = new ArrayList<>();
        for (MoyeoMembers moyeoMember : moyeoMembers) {
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
        savedPost.setVoiceUrl(voiceUrl);
        savedPost.setVoiceLength(durationInSeconds);
        savedPost.setAddress1(addPostReq.getAddress1());
        savedPost.setAddress2(addPostReq.getAddress2());
        savedPost.setAddress3(addPostReq.getAddress3());
        savedPost.setAddress4(addPostReq.getAddress4());
        savedPost.setText(text);
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
        moyeoPublicRepository.deleteMoyeoPost(true);
    }

}
