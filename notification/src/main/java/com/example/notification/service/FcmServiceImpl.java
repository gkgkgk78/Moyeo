package com.example.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;

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


@Service
@Slf4j
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService {

    @Value("${firebase.route}")
    private String route;


    @PostConstruct
    public void initialize() throws Exception {
        // initialize Admin SDK using OAuth 2.0 refresh token

        FileInputStream remoteToken = null;

        try {
//            remoteToken = new FileInputStream("src/main/resources/firebase.json");
            remoteToken = new FileInputStream(route);

        } catch (FileNotFoundException e) {
            log.info(e.getMessage());
            throw new Exception(e.getMessage());
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(remoteToken))
                    .build();
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }

        FirebaseApp.initializeApp(options);
    }


    @Override
    public void send(String token, String content, String title) throws Exception {
        
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle(title) // 알림 제목
                                .setBody(content) // 알림 본문
                                .setIcon("@drawable/bling")
                                .build())
                        .build())
                .putData("requestId", "나야나") // request 식별 정보(requestId) 넣기
                .setToken(token) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();


        // Send a message to the device corresponding to the provided registration token.
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println(response.toString());
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
            throw new Exception(e.getMessage());
        }


    }

    @Override
    public String pushNoti(String deviceToken, String content) throws Exception {
        Instant sendTime = Instant.now().plus(Duration.ofMinutes(10));
        Message message = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(3600 * 1000)
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setDirectBootOk(true)
                        .setNotification(AndroidNotification.builder()
                                .setTitle("Batch에서 Push Test 진행중") // 알림 제목
                                .setBody(content) // 알림 본문
                                .setIcon("@drawable/bling")
                                .build())
                        .build())

                .putData("requestId", "나야나") // request 식별 정보(requestId) 넣기
                .setToken(deviceToken) // 요청자의 디바이스에 대한 registration token으로 설정
                .build();
        log.info("message :{}", message.toString());

        // Send a message to the device corresponding to the provided registration token.
        String response;
        try {
            response = FirebaseMessaging.getInstance().send(message);
            log.info("알림 성공!");
        } catch (FirebaseMessagingException e) {
            log.info("error:{}", e.getMessage());
            log.info("알림 실패");
            throw new Exception(e.getMessage());
        }

        return response;
    }
}
