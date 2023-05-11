package com.example.notification.service;

import com.example.notification.entity.MessageBox;
import com.example.notification.entity.User;
import com.example.notification.repository.MessageBoxRepository;
import com.example.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBoxServiceImpl implements MessageBoxService {


    private final MessageBoxRepository messageBoxRepository;
    private final UserRepository userRepository;

    @Override
    public void insert(String userid, String content) throws Exception {
        MessageBox messageBox = new MessageBox();
        User temp=userRepository.getByUserId(Long.parseLong(userid));
        messageBox.setUserId(userRepository.getByUserId(Long.parseLong(userid)));
        messageBox.setContent(content);
        messageBox.setCreateTime(LocalDateTime.now());
        messageBoxRepository.save(messageBox);
    }


}
