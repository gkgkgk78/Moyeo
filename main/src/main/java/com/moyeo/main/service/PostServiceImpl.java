package com.moyeo.main.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyeo.main.component.AwsS3;
import com.moyeo.main.component.ClovaSpeechClient;
import com.moyeo.main.component.MultiFileToFile;
import com.moyeo.main.dto.*;
import com.moyeo.main.entity.*;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final AwsS3 awsS3;
    private final PostRepository postRepository;
    private final MoyeoPostRepository moyeoPostRepository;
    private final PhotoRepository photoRepository;
    private final FavoriteRepository favoriteRepository;
    private final TimeLineRepository timelineRepository;
    private final UserRepository userRepository;
    private final NationRepository nationRepository;
    private final MultiFileToFile multiFileToFile;
    private final PhotoService photoService;
    private final AsyncTestService asyncTestService;

    // 포스트 생성 및 저장
    @Override
    public Post createPost(AddPostReq addPostReq) throws Exception {
        TimeLine timeline = timelineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));
        if (timeline.getIsComplete() == true) {
            throw new BaseException(ErrorMessage.ALREADY_DONE_TIMELINE);
        }

        Post post = new Post();
        Post savedPost = postRepository.save(post);
        return savedPost;
    }

    // 포스트 속성 값 설정 후 재저장
    @Transactional
    @Override
    public Post insertPost(List<MultipartFile> imageFiles, MultipartFile flagFile, MultipartFile voiceFile, AddPostReq addPostReq) throws Exception {

        Post savedPost = createPost(addPostReq);
        List<Photo> photoList = photoService.createPhotoList(imageFiles, savedPost);


        //파일 형식과 길이를 파악을 하여 post를 등록 시킬지 안시킬지 정하는 부분이다
        checkVoiceFileValidity(voiceFile);

        // voiceFile 길이 추출, Clova로 text 변환, S3에 업로드
        VoiceTextResult voiceResult = getVoiceFileLengthAndTextAndUpload(voiceFile);

        // db에 저장된 국가인 경우 가져와서 사용, 새로운 국가인 경우 nation 저장 후 사용
        Nation nation = findNationOrSaveNation(addPostReq.getAddress1(), flagFile);


        // timeline 객체 가져오기
        TimeLine timeline = timelineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        //푸시 알림을 위해 작업 하는 단계
        User temp_user = userRepository.getByUserId(timeline.getUserId().getUserId());
        PostInsertReq req = PostInsertReq.builder(addPostReq, temp_user).build();
        asyncTestService.test(req);//해당 단계에서 비동기로 푸시 알람을 보낼 예정


        // imageURL, voiceURL db에 저장하기
        log.info("Starting savePost transaction");
        savedPost.setPhotoList(photoList);
        savedPost.setVoiceUrl(voiceResult.getVoiceUrl());
        savedPost.setVoiceLength(voiceResult.getDurationInSeconds());
        // savedPost.setNationUrl(nation.getNationUrl());
        savedPost.setAddress1(addPostReq.getAddress1());
        savedPost.setAddress2(addPostReq.getAddress2());
        savedPost.setAddress3(addPostReq.getAddress3());
        savedPost.setAddress4(addPostReq.getAddress4());
        savedPost.setText(voiceResult.getText());
        savedPost.setTimelineId(timeline);
        savedPost.setNationId(nation);
        savedPost.setFavoriteCount(0L);
        savedPost.setUserId(timeline.getUserId()); //
        Post resavedPost = postRepository.save(savedPost);
        log.info("savePost Transaction complete");

        timeline.setLastPost(resavedPost.getPostId()); //

        return resavedPost;
    }

    public void checkVoiceFileValidity(MultipartFile voiceFile) {
        log.info("voiceFile : {}", voiceFile);
        //파일 형식과 길이를 파악을 하여 post를 등록 시킬지 안시킬지 정하는 부분이다
        String fileName = voiceFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!ext.equals("wav")) {
            System.out.println("웨이브 파일 아님");
            throw new BaseException(ErrorMessage.NOT_PERMIT_VOICE_SAVE);
        }
    }

    public Nation findNationOrSaveNation(String address, MultipartFile flagFile) throws Exception {
        // db에 저장된 국가인 경우 가져와서 사용, 새로운 국가인 경우 nation 저장 후 사용
        String address1 = address;
        Nation nation = nationRepository.findFirstByName(address1);
        if (nation == null) {
            nation = new Nation();
            String flagUrl = awsS3.upload(flagFile, "Moyeo/Nation");
            nation.setNationUrl(flagUrl);
            nation.setName(address1);
            nationRepository.save(nation);
        }

        return nation;
    }

    public VoiceTextResult getVoiceFileLengthAndTextAndUpload(MultipartFile voiceFile) throws Exception {
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

        Files.delete(target);

        int check_time = (int) durationInSeconds;

        if (check_time > 30) {
            throw new BaseException(ErrorMessage.OVER_VOICE_TIME);
        }


        // voiceFile -> text 변환 : clova speech api 요청 보내기
        final ClovaSpeechClient clovaSpeechClient = new ClovaSpeechClient();
        ClovaSpeechClient.NestRequestEntity requestEntity = new ClovaSpeechClient.NestRequestEntity();

        File convertedVoiceFile = multiFileToFile.transTo(voiceFile);
        String result;
        try {
            result = clovaSpeechClient.upload(convertedVoiceFile, requestEntity);
        } finally {
            if (convertedVoiceFile.exists()) { // 로컬에 저장된 파일 삭제
                convertedVoiceFile.delete();
            }
        }

        if (result.contains("\"result\":\"FAILED\"")) {
            throw new BaseException(ErrorMessage.NOT_STT_SAVE);
        }

        log.info("clova result: {}", result);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(result);
        String text = rootNode.get("text").asText();

        // voiceFile -> text 변환 : 응답 결과 확인
        log.info("Clova info :{}", result);
        String voiceUrl = "";

        // 녹음 파일 s3에 업로드
        voiceUrl = awsS3.upload(voiceFile, "Moyeo/Voice");
        log.info("voiceUrl info :{}", voiceUrl);

        return VoiceTextResult.builder().voiceUrl(voiceUrl).durationInSeconds(durationInSeconds).text(text).build();
    }

    @Override
    public Post insertPostTest(Post savedPost, List<Photo> photoList, AddPostReq addPostReq) throws Exception {

        // timeline 객체 가져오기
        TimeLine timeline = timelineRepository.findById(addPostReq.getTimelineId()).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_TIMELINE));

        // db에 저장된 국가인 경우 가져와서 사용, 새로운 국가인 경우 nation 저장 후 사용
        String address1 = addPostReq.getAddress1();
        Nation nation = nationRepository.findFirstByName(address1);
        if (nation == null) {
            nation = new Nation();
            nation.setNationUrl("testurl입니다");
            nation.setName(address1);
            nationRepository.save(nation);
        }


        // imageURL, voiceURL db에 저장하기
        log.info("Starting savePost transaction");
        savedPost.setPhotoList(photoList);
        savedPost.setVoiceUrl("테스트url");
        savedPost.setVoiceLength(77.44);
        // savedPost.setNationUrl(nation.getNationUrl());
        savedPost.setAddress1(addPostReq.getAddress1());
        savedPost.setAddress2(addPostReq.getAddress2());
        savedPost.setAddress3(addPostReq.getAddress3());
        savedPost.setAddress4(addPostReq.getAddress4());
        savedPost.setText("테스트 Text");
        savedPost.setTimelineId(timeline);
        savedPost.setNationId(nation);
        Post resavedPost = postRepository.save(savedPost);
        log.info("savePost Transaction complete");
        return resavedPost;
    }


    // 포스트 삭제 및 해당 포스트의 삭제 사진
    @Override
    @Transactional
    public void deletePostById(Long postId) throws Exception {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_POST));
        for (Photo p : post.getPhotoList()) awsS3.delete(p.getPhotoUrl());
        favoriteRepository.deleteAllByPostId(post);
        photoRepository.deleteAllByPostId(post);
        postRepository.deleteById(postId);
    }

    // 메인 피드에서 포스트 조회
    @Override
    public List<GetPostRes> findByLocation(String location) throws Exception {
        List<Post> posts = postRepository.findAllMainFeedPostByLocation(location); // 완료되지 않은 타임라인의 post 제외 및 공개하지 않은 타임라인의 post 제외
        List<MoyeoPost> moyeoPosts = moyeoPostRepository.findAllMainFeedMoyeoPostByLocation(location); // 완료되지 않은 타임라인의 post 제외 및 공개하지 않은 타임라인의 post 제외
        if ((posts == null || posts.size() == 0) && (moyeoPosts == null || moyeoPosts.size() == 0)) throw new BaseException(ErrorMessage.NOT_EXIST_KEYWORD);

        List<GetPostRes> postList = new ArrayList<>();
        if (posts != null && posts.size() != 0) {
            postList = posts.stream()
                .map(post -> GetPostRes.builder(post, post.getFavoriteCount()).build())
                .collect(Collectors.toList());
        }

        List<GetPostRes> moyeoPostList = new ArrayList<>();
        if (moyeoPosts != null && moyeoPosts.size() != 0) {
            for(MoyeoPost moyeoPost : moyeoPosts) {
                List<TimeLine> timeLineList = timelineRepository.findAllByMoyeoPostId(moyeoPost.getMoyeoPostId());
                if(timeLineList.isEmpty()) continue;

                moyeoPostList.add(GetPostRes.builder(moyeoPost, timeLineList).build());
            }
        }

        postList.addAll(moyeoPostList);
        Collections.sort(postList, Comparator.comparing(GetPostRes::getCreateTime, Comparator.reverseOrder()));

        return postList;
    }

    // 내 페이지에서 포스트 조회
    @Override
    public List<GetPostRes> findMyPost(String location, Long userUid) throws Exception {
        User user = userRepository.findById(userUid).orElseThrow(() -> new BaseException(ErrorMessage.NOT_EXIST_USER));

        List<Post> posts = postRepository.findAllMyPostByLocation(location, userUid); // 내 포스트 중에서 timeline이 완성되지 않은 post 제외
        List<MoyeoPost> moyeoPosts = moyeoPostRepository.findAllMyMoyeoPostByLocation(location, userUid); // 내 포스트 중에서 timeline이 완성되지 않은 post 제외 + 삭제된 모여포스트 제외
        if ((posts == null || posts.size() == 0) && (moyeoPosts == null || moyeoPosts.size() == 0)) throw new BaseException(ErrorMessage.NOT_EXIST_KEYWORD);

        List<GetPostRes> postList = new ArrayList<>();
        for (Post post : posts) {
            postList.add(GetPostRes.builder(post, post.getFavoriteCount()).build());
        }

        List<GetPostRes> moyeoPostList = new ArrayList<>();
        if (moyeoPosts != null && moyeoPosts.size() != 0) {
            for(MoyeoPost moyeoPost : moyeoPosts) {
                List<TimeLine> timeLineList = timelineRepository.findAllMineByMoyeoPostId(moyeoPost.getMoyeoPostId(), userUid);
                if(timeLineList.isEmpty()) continue;

                moyeoPostList.add(GetPostRes.builder(moyeoPost, timeLineList).build());
            }
        }

        postList.addAll(moyeoPostList);
        Collections.sort(postList, Comparator.comparing(GetPostRes::getCreateTime, Comparator.reverseOrder()));

        return postList;
    }

}
