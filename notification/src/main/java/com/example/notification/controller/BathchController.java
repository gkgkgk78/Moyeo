package com.example.notification.controller;

import com.example.notification.service.FcmService;
import com.example.notification.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FcmService fcmService;
    private final UserService userService;
    @PostMapping(value = "/push")
    ResponseEntity<?> pushNotification(@RequestBody Map<String,String> map) throws Exception {
        String deviceToken = map.get("deviceToken");
        String message = map.get("message");
        log.info("Batch Restaurant Reco TO :{}",deviceToken);
        log.info("Batch Restaurant Reco MESSAGE :{}",message);
        String result = fcmService.pushNoti(deviceToken,message);
        log.info("Batch Restaurant Reco result :{}",result);
        Long id = userService.findByDeviceToken(deviceToken);
        log.info("해당 id : {}",id);
        Map<String,String> responseMap = Map.of("result",result,"id",id+"");
        return ResponseEntity.ok(responseMap);
    }
}
