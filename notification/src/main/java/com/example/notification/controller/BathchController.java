package com.example.notification.controller;

import com.example.notification.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/batch")
@RequiredArgsConstructor
@RestController
@Slf4j
public class BathchController {
    private final FcmService fcmService;
    @PostMapping(value = "/push")
    ResponseEntity<?> pushNotification(String deviceToken, String message) throws Exception {
        log.info("Batch Restaurant Reco TO :{}",deviceToken);
        log.info("Batch Restaurant Reco MESSAGE :{}",message);
        String result = fcmService.pushNoti(deviceToken,message);
        log.info("Batch Restaurant Reco result :{}",result);
        return ResponseEntity.ok(result);
    }
}
