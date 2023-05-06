package com.moyeo.main.service;

import com.moyeo.main.dto.ChatReq;
import com.moyeo.main.entity.Chat;
import com.moyeo.main.exception.BaseException;

import java.util.List;

public interface ChatService {

    void insert(String name , ChatReq chat)throws BaseException;
    List<Chat> select(String name) throws BaseException;
}