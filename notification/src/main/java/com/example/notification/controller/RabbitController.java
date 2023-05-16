package com.example.notification.controller;


import com.example.notification.dto.PostInsertReq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.AMQImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.impl.AMQImpl.Queue.DeclareOk;

import javax.annotation.PostConstruct;


@RequestMapping("/rabbit")
@RequiredArgsConstructor
@RestController
@Slf4j
public class RabbitController {


    private static final String EXCHANGE_NAME = "sample.exchange";


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;


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
        req.setDeviceToken("1234");


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(Integer.parseInt(port));
        factory.setPassword(password);
        factory.setUsername(username);

        try {
            // RabbitMQ 서버에 연결
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            // 큐 선언
            AMQImpl.Queue.DeclareOk queueDeclareOk = (DeclareOk) channel.queueDeclarePassive("sample.queue");

            // 큐의 메시지 수 확인
            int messageCount = queueDeclareOk.getMessageCount();
            System.out.println("Queue '" + "sample.queue" + "'의 메시지 수: " + messageCount);


            // 연결 종료
            channel.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        rabbitTemplate.convertAndSend(EXCHANGE_NAME, "key1", req);

        System.out.println("보내짐");


        return "message sending!";
    }


}
