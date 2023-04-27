package com.moyeo.service;

import com.moyeo.config.security.JwtTokenProvider;
import com.moyeo.conponent.AwsS3;
import com.moyeo.dto.UserLoginReq;
import com.moyeo.dto.TokenRes;
import com.moyeo.dto.UserInfoRes;
import com.moyeo.entity.User;
import com.moyeo.repository.TimeLineRedisRepository;
import com.moyeo.repository.TimeLineRepository;
import com.moyeo.repository.UserRepository;
import com.moyeo.utils.HttpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TimeLineRepository timeLineRepository;
    private final TimeLineService timeLineService;
    public final JwtTokenProvider jwtTokenProvider;
    public final PasswordEncoder passwordEncoder;
    private final AwsS3 awsS3;
    private final TimeLineRedisRepository repo;

    @Override
    public List<UserInfoRes> searchUserByNickname(String search) {
        List<User> resultList = userRepository.searchUserByNickname(search);
        List<UserInfoRes> returnList = new ArrayList<>();

        for (User user : resultList) {
            returnList.add(entityToResponseDTO(user));
        }
        return returnList;
    }


    // 카카오 로그인 연동
    public TokenRes signUpKakao(UserLoginReq userLoginReq) throws JsonProcessingException {

//         카카오톡 rest api (id, profile image, nickname)
        HttpHeaders headers = HttpUtil.generateHttpHeadersForJWT(userLoginReq.getAccessToken());
        RestTemplate restTemplate = HttpUtil.generateRestTemplate();

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class);

        JsonNode json = new ObjectMapper().readTree(response.getBody());

        String clientId = json.get("id").asText();
        String profileImageUrl = json.get("kakao_account").get("profile").get("profile_image_url").asText();
        String nickname = json.get("kakao_account").get("profile").get("nickname").asText();

        User user;
//        String clientId = "2725446611";
//        String nickname = "송지율";
//        String profileImageUrl = "http://k.kakaocdn.net/dn/dIUOxh/btrOfbMpO9p/JikTtvK5PtI5Wi6RMyPkDK/img_640x640.jpg";

        // 카카오에서 받아 온 데이터(clientId)로 이미 등록된 유저인지 확인
        if (userRepository.getByClientId(clientId) != null) {
            user = userRepository.getByClientId(clientId);
            if (!passwordEncoder.matches("다님", user.getPassword())) {
                throw new RuntimeException();
            }
            TokenRes tokenRes = jwtTokenProvider.createtoken(clientId, "USER");
            userRepository.findByClientId(clientId).setRefreshToken(tokenRes.getRefreshToken());
            return tokenRes;
        }

        // 미등록 사용자
        TokenRes tokenRes = jwtTokenProvider.createtoken(clientId, "USER");
        user = User.builder()
                .nickname(nickname) // 'nickname' 값을 nickname에 저장
                .clientId(clientId) // 'id' 값을 clientId에 저장
                .role("USER")
                .refreshToken(tokenRes.getRefreshToken())
                .profileImageUrl(profileImageUrl)
                .password(passwordEncoder.encode("다님"))
                .build();
        userRepository.save(user);
        return tokenRes;
    }

    @Override
    public UserInfoRes updateUserInfo(Long userUid, MultipartFile profileImage, String nickname) throws Exception {
        User user = userRepository.getByUserUid(userUid);

        if (profileImage == null) {
            log.info("프로필 이미지 변경 X, 닉네임만 변경");
            // 프로필 이미지 변경 X, 닉네임만 변경
            user.setNickname(nickname);
        } else {
            // 프로필 이미지 변경 및 닉네임 변경
            log.info("프로필 이미지 변경 및 닉네임 변경");
            // 프로필 이미지 S3에 업로드 및 imageURL 가져오기
            String ProfileImageUrl = awsS3.upload(profileImage, "Danim/profile");

            // 이전 프로필 이미지 Url -> s3에서 삭제
            String beforeProfileImageUrl = user.getProfileImageUrl();
            awsS3.delete(beforeProfileImageUrl);
            user.setProfileImageUrl(ProfileImageUrl);
            user.setNickname(nickname);
        }
        repo.deleteAll();
        return entityToResponseDTO(user);
    }

    @Override
    public UserInfoRes getNicknameAndProfileImage(Long userUid) throws Exception {
        User user = userRepository.getByUserUid(userUid);

        return entityToResponseDTO(user);
    }

    // User 객체를 UserInfoRes로 변환
    private UserInfoRes entityToResponseDTO(User user) {
        Integer timelineNum = timeLineRepository.countAllByUserUid(user);
        Long timeLineId = -1L;
        if(timeLineService.isTraveling(user.getUserUid()) != null){
            timeLineId = timeLineService.isTraveling(user.getUserUid()).getTimelineId();
        }

        return UserInfoRes.builder()
                .userUid(user.getUserUid())
                .nickname(user.getNickname())
                .profileImageUrl((user.getProfileImageUrl()))
                .timeLineId(timeLineId)
                .timelineNum(timelineNum)
                .build();
    }


    public Boolean signUp() {
        // 카카오톡 rest api (id, profile image, nickname)

        User user;
        String clientId = "1234";
        String nickname = "테스트";
        String profileImageUrl = "----";

        // 카카오에서 받아 온 데이터(clientId)로 이미 등록된 유저인지 확인
        if (userRepository.getByClientId(clientId) != null) {
            user = userRepository.getByClientId(clientId);
            if (!passwordEncoder.matches("다님", user.getPassword())) {
                throw new RuntimeException();
            }
            TokenRes tokenRes = jwtTokenProvider.createtoken(clientId, "USER");
            userRepository.findByClientId(clientId).setRefreshToken(tokenRes.getRefreshToken());
            return true;
        }

        // 미등록 사용자
        TokenRes tokenRes = jwtTokenProvider.createtoken(clientId, "USER");
        user = User.builder()
                .nickname(nickname) // 'nickname' 값을 nickname에 저장
                .clientId(clientId) // 'id' 값을 clientId에 저장
                .role("USER")
                .refreshToken(tokenRes.getRefreshToken())
                .profileImageUrl(profileImageUrl)
                .password(passwordEncoder.encode("다님"))
                .build();
        userRepository.save(user);
        return true;
    }


}
