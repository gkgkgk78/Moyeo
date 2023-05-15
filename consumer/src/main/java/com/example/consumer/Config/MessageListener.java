package com.example.consumer.Config;

import com.example.consumer.dto.BatchMessage;
import com.example.consumer.dto.PostInsertReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageListener {
//    @RabbitListener(queues = "sample.queue")
//    public void receiveMessage(Message message){
//        log.info("Mesaage info : {}",message);
//    }

    @RabbitListener(queues = "sample.queue")
    public void receiveMessage(PostInsertReq post) {
        PostInsertReq r=post;

        System.out.println("받음");
        log.info(post.toString());
        log.info("Mesaage info : {}", "받음");
    }
    
    
    

    @RabbitListener(queues = "batch.queue")
    public void receiveMessageFromBatch(BatchMessage message){
        log.info("ReceiveMessageFromBatch Server message info : {}",message);
        log.info("ReceiveMessageFromBatch Server message info : {}",message.toString());
//        log.info("ReceiveMessageFromBatch Server message.getMessageProperties() info : {}",message.getMessageProperties());
//        log.info("ReceiveMessageFromBatch Server message.getBody() info : {}",message.getBody());
    }
}
