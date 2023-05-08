package com.moyeo.main.controller;

import com.moyeo.main.dto.MyMessageBoxDTO;
import com.moyeo.main.entity.User;
import com.moyeo.main.service.MessageBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/auth/message")
@RestController
@Log4j2
public class MessageBoxController {

    private final MessageBoxService messageBoxService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMessagesByUser(@PathVariable Long userId) {
        log.info("메시지함 조회 로직 시작");
        //로그인 정보에서 uid 받아오기
//        Long userId = null;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth != null && auth.getPrincipal() != null) {
//            User user = (User) auth.getPrincipal();
//            userId = user.getUserId();
//        }
        List<MyMessageBoxDTO> myMessageBoxDTOS = messageBoxService.getMessagesByUser(userId);

        log.info("메시지함 조회 로직 완료");
        return new ResponseEntity<>(myMessageBoxDTOS, HttpStatus.OK);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<?> markAsCheckedByMessageId(@PathVariable Long messageId) {
        log.info("메시지 1건 조회 로직 시작");
        messageBoxService.markAsCheckedByMessageId(messageId);
        log.info("메시지 1건 조회 로직 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/readall/{userId}")
    public ResponseEntity<?> markAsCheckedAllByUserId(@PathVariable Long userId) {
        log.info("메시지 전체 조회처리 로직 시작");
        //로그인 정보에서 uid 받아오기
//        Long userId = null;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if(auth != null && auth.getPrincipal() != null) {
//            User user = (User) auth.getPrincipal();
//            userId = user.getUserId();
//        }
        messageBoxService.markAsCheckedAll(userId);
        log.info("메시지 전체 조회처리 로직 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> DeletedMessageById(@PathVariable Long messageId) {
        log.info("메시지 1건 삭제 로직 시작");
        messageBoxService.RemoveMessage(messageId);
        log.info("메시지 1건 삭제 로직 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
