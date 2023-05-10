package com.moyeo.main.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;


@Service
@Slf4j
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    @Value("${firebase.route}")
    private String route;



    @PostConstruct
    public void initialize() throws BaseException {
        // initialize Admin SDK using OAuth 2.0 refresh token

        FileInputStream remoteToken = null;

        try {
    //        remoteToken = new FileInputStream("src/main/resources/firebase.json");
            remoteToken = new FileInputStream(route);
        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
            throw new BaseException(ErrorMessage.NOT_EXIST_ROUTE);
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(remoteToken))
                    .build();
        } catch (IOException e) {
            throw new BaseException(ErrorMessage.FIREBASE_INIT_ERROR);
        }

        FirebaseApp.initializeApp(options);
    }


    @Override
    public void send(User user) throws BaseException {
//      device 토큰값 갱신반영이 안될 경우 메시지전송 에러 발생함 : 현재 갱신기준(: 앱 재설치)
        String token = user.getDeviceToken();
        Instant sendTime = Instant.now().plus(Duration.ofMinutes(10));
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
//                        .setRestrictedPackageName("com.moyeo.moyeo") // 애플리케이션 패키지 이름
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("여봇이 생각을 마쳤습니다!") // 알림 제목
                                .setBody("결과를 확인하러 갈까요?") // 알림 본문
                                .setIcon("@drawable/bling")
                                .build())

                        .build())
                .putData("requestId", user.getUserId().toString()) // request 식별 정보(requestId) 넣기
                .setToken(token) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();


        // Send a message to the device corresponding to the provided registration token.
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println(response.toString());
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
            throw new BaseException(ErrorMessage.FIREBASE_SEND_ERROR);
        }


    }
}
