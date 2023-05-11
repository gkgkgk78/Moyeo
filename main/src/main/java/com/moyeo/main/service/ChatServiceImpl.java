package com.moyeo.main.service;

import com.moyeo.main.dto.ChatReq;
import com.moyeo.main.entity.Chat;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
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

        } catch (Exception e) {
            log.info(e.getMessage());

        }
    }

    @Override
    public void insertResponse(User user, String result) throws BaseException {

        try {
            log.info("MongoDB에 저장 로직 시작");
            Chat chat = new Chat();
            chat.setMessage(result);
            chat.setSender("gpt");
            chat.setCreateTime(LocalDateTime.now());
            mongoTemplate.insert(chat, user.getUserId().toString());
            log.info("MongoDB에 저장 완료");
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BaseException(ErrorMessage.MONGO_DB_ERROR);
        }
    }


    public List<Chat> select(String name) throws BaseException {

        List<Chat> findData = mongoTemplate.findAll(Chat.class, name);
        return findData;
    }

}
