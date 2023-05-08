package com.example.notification.service;

import com.example.notification.dto.ChatReq;


public interface ChatService {

    void insert(String name , ChatReq chat)throws Exception;
    //List<Chat> select(String name) throws BaseException;
}
