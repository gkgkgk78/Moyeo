package com.moyeo.main.controller;

import com.moyeo.main.dto.ChangeFavoriteStatusReq;
import com.moyeo.main.dto.ChangeFavoriteStatusRes;
import com.moyeo.main.dto.ChatReq;
import com.moyeo.main.entity.Chat;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.service.ChatService;
import com.moyeo.main.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/auth/chat")
@RestController
@Slf4j
public class ChatController {


    private ChatService chatService;

    @PostMapping("")
    public ResponseEntity<?> insertChat(@RequestBody ChatReq chat) throws Exception {

        //insert 작업의 첫번째 파라미터는 인증된 사용자의 고유한 닉네임 값이 들어갈 것임
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();
    
        log.info("chat insert작업 시작");
        chatService.insert(user.getUserId().toString(), chat);
        log.info("chat insert작업 완료");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> selectChat() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth != null && auth.getPrincipal() != null)
            user = (User) auth.getPrincipal();

        List<Chat> result = chatService.select(user.getUserId().toString());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
