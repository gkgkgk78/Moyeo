package com.example.notification.controller;


import com.example.notification.dto.PostInsertReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        //id , message, devicetoken

        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "sample.key", "테스트진행중");

        return "message sending!";
    }

    @GetMapping("/publish2")
    public String samplePublish1() {

        //id , message, devicetoken

        PostInsertReq req = new PostInsertReq();
        req.setAddress1("서울시");
        req.setAddress2("강남구");
        req.setAddress3("역삼동");
        req.setAddress4("테헤란로");
        req.setUserId(8L);
        req.setDeviceToken("fHBwmuPeRgubZ1-8oIXeaT:APA91bHt6PO4fWkfIyQNnVzamIwF3usHKmVwhEXrXOzbEsia8_ZY_vCvbM8z5nHzqwzSyFDDvvCDy2CRddHNEQucfEIe279HdvTmgTdBYcSK8J4LDs999zVTt19XNNMtyWG5SklohmxF");
//
//        public void convertAndSend(String exchange, String routingKey, final Object object) throws AmqpException {
//            convertAndSend(exchange, routingKey, object, (CorrelationData) null);
//        }
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "key1", req);
        System.out.println("보내짐");

        return "message sending!";
    }


}
