package com.moyeo.main.service;

import com.moyeo.main.entity.Chat;
import com.moyeo.main.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    public void insert(String name, Chat chat) throws BaseException {
        chat.setCreateTime(LocalDateTime.now());
        log.info("chat inser 시작");
        log.info(name.toString());
        try {
            mongoTemplate.insert(chat, name);
        }
        catch (Exception e)
        {
            log.info(e.getMessage());

        }
    }


    public List<Chat> select(String name) throws BaseException {

        List<Chat> findData = mongoTemplate.findAll(Chat.class, name);
        return findData;
    }

}
