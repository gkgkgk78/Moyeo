package com.moyeo.main.service;

import com.moyeo.main.dto.ChatReq;
import com.moyeo.main.entity.Chat;
import com.moyeo.main.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void insert(String name, ChatReq chat1) throws BaseException {


        log.info("chat insert 시작");
        //log.info(name.toString());
        try {
            //사용자의 응답을 mongo db에 넣는 중
            for (String s1 : chat1.getMessage()) {
                Chat chat = new Chat();
                chat.setMessage(s1);
                chat.setSender(chat1.getSender());
                chat.setCreateTime(LocalDateTime.now());
                mongoTemplate.insert(chat, name);
            }

            //사용자의 질문 목록을 auto gpt에 날리기


            //받은 응답을 기반으로 푸시 알람 날리기


        } catch (Exception e) {
            log.info(e.getMessage());

        }
    }


    public List<Chat> select(String name) throws BaseException {

        List<Chat> findData = mongoTemplate.findAll(Chat.class, name);
        return findData;
    }

}
