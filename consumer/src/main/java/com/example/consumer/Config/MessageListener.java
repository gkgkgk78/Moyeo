package com.example.consumer.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {
    @RabbitListener(queues = "sample.queue")
    public void receiveMessage(Message message){
        log.info("Mesaage info : {}",message);
    }

    @RabbitListener(queues = "batch.queue")
    public void receiveMessageFromBatch(Message message){
        log.info("ReceiveMessageFromBatch Server info : {}",message);
    }
}
