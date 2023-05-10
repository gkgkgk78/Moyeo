package com.example.notification.controller;

import com.example.notification.service.FcmService;
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
    @PostMapping(value = "/push")
    ResponseEntity<?> pushNotification(@RequestBody Map<String,String> map) throws Exception {
        String deviceToken = map.get("deviceToken");
        String message = map.get("message");
        log.info("Batch Restaurant Reco TO :{}",deviceToken);
        log.info("Batch Restaurant Reco MESSAGE :{}",message);
        String result = fcmService.pushNoti(deviceToken,message);
        log.info("Batch Restaurant Reco result :{}",result);
        return ResponseEntity.ok(result);
    }
}
