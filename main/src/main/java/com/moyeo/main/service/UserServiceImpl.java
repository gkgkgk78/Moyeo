package com.moyeo.main.service;

import com.moyeo.main.config.security.JwtTokenProvider;
import com.moyeo.main.conponent.AwsS3;
import com.moyeo.main.dto.GetPostRes;
import com.moyeo.main.dto.MoyeoPostStatusDto;
import com.moyeo.main.dto.UserLoginReq;
import com.moyeo.main.dto.TokenRes;
import com.moyeo.main.dto.UserInfoRes;
import com.moyeo.main.entity.MoyeoMembers;
import com.moyeo.main.entity.User;
import com.moyeo.main.repository.MoyeoMembersRepository;
import com.moyeo.main.repository.TimeLineRepository;
import com.moyeo.main.repository.UserRepository;
import com.moyeo.main.utils.HttpUtil;
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
import java.util.stream.Collectors;

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
    // private final TimeLineRedisRepository repo;
    private final MoyeoMembersRepository moyeoMembersRepository;

    @Override
    public List<UserInfoRes> searchUserByNickname(String search, Boolean isMoyeo) {
        if(isMoyeo) {
            log.info("동행 중인 유저들 제외하고 검색 중...");
            List<UserInfoRes> resultList = userRepository.searchUserByNickname(search).stream()
                .filter(user -> {
                    // 이미 동행 중인 유저들은 검색 조회 대상에서 제외된다.
                    return moyeoMembersRepository.findFirstByUserIdAndFinishTime(user, null).isEmpty();
                })
                .map(user -> entityToResponseDTO(user))
                .collect(Collectors.toList());

            log.info("동행 중인 유저들 제외하고 검색 완료...");
            return resultList;
        }

        List<User> resultList = userRepository.searchUserByNickname(search);

        List<UserInfoRes> returnList = new ArrayList<>();

        for (User user : resultList) {
            returnList.add(entityToResponseDTO(user));
        }
        return returnList;
    }


    // 카카오 로그인 연동
    public TokenRes signUpKakao(UserLoginReq userLoginReq) throws JsonProcessingException {

//      카카오톡 rest api (id, profile image, nickname)
        HttpHeaders headers = HttpUtil.generateHttpHeadersForJWT(userLoginReq.getAccessToken());
        RestTemplate restTemplate = HttpUtil.generateRestTemplate();

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, String.class);

        JsonNode json = new ObjectMapper().readTree(response.getBody());

        String clientId = json.get("id").asText();
        String profileImageUrl = json.get("kakao_account").get("profile").get("profile_image_url").asText();
        String nickname = json.get("kakao_account").get("profile").get("nickname").asText();
        log.info("nickname: {}", nickname);
        log.info("기기토큰", userLoginReq.getAccessToken());

        User user;

        log.info("카카오 로그인 중... device token : {}", userLoginReq.getDeviceToken());
        if(userLoginReq.getDeviceToken() != null){
            log.info("카카오 로그인 중... device token 길이 : {}", userLoginReq.getDeviceToken().length());
        } else {
            log.info("카카오 로그인 중... device token = null...!!");
        }

        // 카카오에서 받아 온 데이터(clientId)로 이미 등록된 유저인지 확인
        if (userRepository.getByClientId(clientId) != null) {
            user = userRepository.getByClientId(clientId);
            if (!passwordEncoder.matches("모여", user.getPassword())) {
                throw new RuntimeException();
            }
            TokenRes tokenRes = jwtTokenProvider.createtoken(clientId, "USER");
            User updateUser = userRepository.findByClientId(clientId);
            updateUser.setRefreshToken(tokenRes.getRefreshToken());
            if (userLoginReq.getDeviceToken() != null && userLoginReq.getDeviceToken().length() != 0) {
                updateUser.setDeviceToken(userLoginReq.getDeviceToken());
            }
            userRepository.save(updateUser);

            return tokenRes;
        }

        // 미등록 사용자
        TokenRes tokenRes = jwtTokenProvider.createtoken(clientId, "USER");
        log.info("리프레시 토큰 {}, 길이: {}", tokenRes.getRefreshToken(), tokenRes.getRefreshToken().length());
        user = User.builder()
                .nickname(nickname) // 'nickname' 값을 nickname에 저장
                .clientId(clientId) // 'id' 값을 clientId에 저장
                .role("USER")
                .refreshToken(tokenRes.getRefreshToken())
                .profileImageUrl(profileImageUrl)
                .password(passwordEncoder.encode("모여"))
                .deviceToken(userLoginReq.getDeviceToken())
                .build();
        userRepository.save(user);
        return tokenRes;
    }

    @Override
    public UserInfoRes updateUserInfo(Long userUid, MultipartFile profileImage, String nickname) throws Exception {
        User user = userRepository.getByUserId(userUid);

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
        // repo.deleteAll();
        return entityToResponseDTO(user);
    }

    @Override
    public UserInfoRes getNicknameAndProfileImage(Long userUid) throws Exception {
        User user = userRepository.getByUserId(userUid);

        return entityToResponseDTO(user);
    }

    // User 객체를 UserInfoRes로 변환
    private UserInfoRes entityToResponseDTO(User user) {
        Integer timelineNum = timeLineRepository.countAllByUserId(user);
        Long timeLineId = -1L;
        if (timeLineService.isTraveling(user.getUserId()) != null) {
            timeLineId = timeLineService.isTraveling(user.getUserId()).getTimelineId();
        }

        return UserInfoRes.builder()
                .userUid(user.getUserId())
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
            if (!passwordEncoder.matches("모여", user.getPassword())) {
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
                .password(passwordEncoder.encode("모여"))
                .build();
        userRepository.save(user);

        return true;
    }


}
