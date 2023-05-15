package com.example.notification.controller;

import com.example.notification.dto.BatchMessage;
import com.example.notification.service.FcmService;
import com.example.notification.service.UserService;
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

@RequestMapping("/batch")
@RequiredArgsConstructor
@RestController
@Slf4j
public class BathchController {
    @Value("${batch-exchange}")
    private  String EXCHANGE_NAME;
    @Value("${batch-route-key}")
    private  String ROUTING_KEY;
    private final RabbitTemplate rabbitTemplate;
    private final FcmService fcmService;
    private final UserService userService;

    @PostMapping(value = "/push")
    ResponseEntity<?> pushNotification(@RequestBody Map<String,String> map) throws Exception {
        String deviceToken = map.get("deviceToken");
        String message = map.get("message");
        log.info("Batch Restaurant Reco TO :{}",deviceToken);
        log.info("Batch Restaurant Reco MESSAGE :{}",message);
        log.info("EXCHAGE NAME :{}",EXCHANGE_NAME);
        log.info("ROUTING KET :{}",ROUTING_KEY);
//        String result = fcmService.pushNoti(deviceToken,message);
//        log.info("Batch Restaurant Reco result :{}",result);
        Long id = userService.findByDeviceToken(deviceToken);
        log.info("해당 id : {}",id);
//        map.put("id",id+"");
        BatchMessage batchMessage = BatchMessage.builder().id(id).message(message).deviceToken(deviceToken).build();
        log.info("BatchMessage  info : {}",batchMessage);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,batchMessage);
//        Map<String,String> responseMap = Map.of("result",result,"id",id+"");
        return ResponseEntity.ok("responseMap");
    }
}
