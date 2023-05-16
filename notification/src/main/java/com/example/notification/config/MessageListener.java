package com.example.notification.config;


import com.example.notification.dto.BatchMessage;
import com.example.notification.dto.PostInsertReq;
import com.example.notification.service.PostInsertAutogpt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = {"sample.queue"})
public class MessageListener {


    private final PostInsertAutogpt postInsertAutogpt;



    @RabbitHandler
    public void receiveMessage(PostInsertReq post) {
        log.info("비동기 post insert 후 autogpt작업 시작");
        postInsertAutogpt.insert(post);
        log.info("비동기 post insert 후 autogpt작업 완료");
    }

    @RabbitHandler
    public void receiveBatchMessage(BatchMessage batchMessage) {

    }


//    @RabbitListener(queues = "batch.queue")
//    public void receiveMessageFromBatch(Message message) {
//        log.info("ReceiveMessageFromBatch Server info : {}", message);
//    }

}
