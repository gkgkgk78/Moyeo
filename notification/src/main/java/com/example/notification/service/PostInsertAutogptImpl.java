package com.example.notification.service;

import com.example.notification.component.YeobotClient;
import com.example.notification.dto.PostInsertReq;
import com.example.notification.entity.Chat;
import com.example.notification.entity.User;
import com.example.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;


@Log4j2
@Service
@RequiredArgsConstructor
public class PostInsertAutogptImpl implements PostInsertAutogpt {

    private final YeobotClient yeobotClient;
    private final FcmService fcmService;

    private final UserRepository userRepository;

    private final ChatService chatService;

    @Override
    public void insert(PostInsertReq post) {

        //해당 요청이 들어오면, 액티비티, 여행지, 맛집 세개를 추천을 해줘야 함
        //autogpt에게 응답을 보낸후
        String goal = "Search for a good restaurant near " + post.getAddress1() + " " + post.getAddress2() + " " + post.getAddress3() + " " + post.getAddress4() + ".";

        try {

            String a1 = yeobotClient.sendYeobotData("restaurant", goal);
            log.info("autogpt에게 응답 받음 " + a1.toString());
            System.out.println(a1.toString());
            //받은 응답을 바탕으로 푸시 알림을 해줘야 함
            fcmService.send(post.getDeviceToken(), a1);
            User user = userRepository.getByUserId(post.getUserId());
            Chat chat = Chat.builder(a1).build();
            chatService.insertAutogpt(post.getUserId().toString(), chat);


        } catch (Exception e) {
            log.info(e.getMessage());
            return;
        }


    }


}
