package com.example.notification.service;


import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;


@Component
public class SampleListener {

    private static final Logger log = LoggerFactory.getLogger(SampleListener.class);
    @RabbitListener(queues = "sample.queue")
    public void receiveMessage(final Message message) {
        log.info(message.toString());
    }
}