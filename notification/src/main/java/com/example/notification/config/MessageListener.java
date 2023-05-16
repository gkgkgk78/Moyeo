package com.example.notification.config;


import com.example.notification.dto.BatchMessage;
import com.example.notification.dto.PostInsertReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RabbitListener(queues = {"sample.queue"})
public class MessageListener {

    @RabbitHandler
    public void receiveMessage(PostInsertReq post) {
        PostInsertReq r = post;

        System.out.println("받음");
        log.info(post.toString());
        log.info("Mesaage info : {}", "받음");
    }

    @RabbitHandler
    public void receiveBatchMessage(BatchMessage batchMessage) {

    }


//    @RabbitListener(queues = "batch.queue")
//    public void receiveMessageFromBatch(Message message) {
//        log.info("ReceiveMessageFromBatch Server info : {}", message);
//    }

}
