package com.example.notification.controller;


import com.example.notification.service.FcmService;
import com.example.notification.service.FcmServiceImpl;
import com.example.notification.service.PostInsertAutogpt;
import com.example.notification.dto.PostInsertReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/autogpt")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AutogptController {


    private final PostInsertAutogpt postInsertAutogpt;

    @PostMapping(value = "")
    public void getFavoritePostList(@ModelAttribute PostInsertReq req) throws Exception {
        log.info("비동기 post insert 후 autogpt작업 시작");
        postInsertAutogpt.insert(req);
        log.info("비동기 post insert 후 autogpt작업 완료");
    }



}
