package com.example.notification.service;

import com.example.notification.dto.ChatReq;
import com.example.notification.entity.Chat;


public interface ChatService {

    void insertRecv(String name , ChatReq chat)throws Exception;

    void insertAutogpt(String name , Chat chat)throws Exception;
    //List<Chat> select(String name) throws BaseException;
}
