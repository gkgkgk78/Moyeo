package com.example.notification.service;

import com.example.notification.dto.ChatReq;
import com.example.notification.entity.Chat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void insertRecv(String name, ChatReq chat1) throws Exception {

        log.info("chat insert 시작");
        //log.info(name.toString());

        try {
            for (String s1 : chat1.getMessage()) {
                Chat chat = new Chat();
                chat.setMessage(s1);
                chat.setSender(chat1.getSender());
                chat.setCreateTime(LocalDateTime.now());
                mongoTemplate.insert(chat, name);
            }

        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }

    @Override
    public void insertAutogpt(String name, Chat chat) throws Exception {
        try {
            mongoTemplate.insert(chat, name);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

    }


}
