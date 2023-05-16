package com.example.notification.service;

import com.example.notification.dto.BatchMessage;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface BatchAutogpt {

    void insert(BatchMessage message) throws Exception;
}
