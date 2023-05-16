package com.example.sender.Controller;

import com.example.sender.DTO.BatchMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping
@RequiredArgsConstructor
@RestController
@Slf4j
public class BatchController {
    @Value("${batch-exchange}")
    private  String EXCHANGE_NAME;
    @Value("${batch-route-key}")
    private  String ROUTING_KEY;
    private final RabbitTemplate rabbitTemplate;
    @PostMapping(value = "/push")
    ResponseEntity<?> pushNotification(@RequestBody Map<String,String> map) throws Exception {
        BatchMessage batchMessage = BatchMessage.builder().Title(map.get("Title")).userId(Long.valueOf(map.get("userId")))
                        .deviceToken(map.get("deviceToken")).address1(map.get("address1")).address2(map.get("address2"))
                        .address3(map.get("address3")).address4(map.get("address4")).build();
        log.info("BatchMessage :{}",batchMessage);

        log.info("EXCHAGE NAME :{}",EXCHANGE_NAME);
        log.info("ROUTING KET :{}",ROUTING_KEY);
//        messageSender.sendMessageBatchMessage(batchMessage);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,ROUTING_KEY,batchMessage);
        return ResponseEntity.ok("Queue에 보냈습니다.");
    }

}
