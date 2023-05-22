package com.example.notification.service;


import com.example.sender.DTO.BatchMessage;

public interface BatchAutogpt {

    void insert(BatchMessage message) throws Exception;

    void insertSecond(BatchMessage batchMessage) throws Exception;
}
