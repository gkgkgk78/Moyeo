package com.example.sender.Controller;

import com.example.sender.DTO.PostInsertReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/autogpt")
@RequiredArgsConstructor
@RestController
@Slf4j

public class AutoGPTController {
    @Value("${batch-exchange}")
    private  String EXCHANGE_NAME;
    @Value("${batch-route-key}")
    private  String ROUTING_KEY;
    private final RabbitTemplate rabbitTemplate;

    @PostMapping(value = "")
    public void getFavoritePostList(@ModelAttribute PostInsertReq req) throws Exception {
        log.info("PostinsertReq Queue 등록");
//        postInsertAutogpt.insert(req);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,ROUTING_KEY,req);

        log.info("PostinsertReq Queue 등록 완료");
    }

}
