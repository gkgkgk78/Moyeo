package com.example.notification.controller;


import com.example.notification.dto.PostInsertReq;
import com.example.notification.service.PostInsertAutogpt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/rabbit")
@RequiredArgsConstructor
@RestController
@Slf4j
public class RabbitController {


    private static final String EXCHANGE_NAME = "sample.exchange";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/publish1")
    public String samplePublish() {

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sample.key", "테스트진행중");
        return "message sending!";
    }


}
