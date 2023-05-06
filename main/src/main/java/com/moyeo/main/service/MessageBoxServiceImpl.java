package com.moyeo.main.service;

import com.moyeo.main.dto.MyMessageBoxDTO;
import com.moyeo.main.entity.MessageBox;
import com.moyeo.main.entity.User;
import com.moyeo.main.repository.MessageBoxRepository;
import com.moyeo.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBoxServiceImpl implements MessageBoxService{

    private final MessageBoxRepository messageBoxRepository;
    private final UserRepository userRepository;

    @Override
    public List<MyMessageBoxDTO> getMessagesByUser(Long userId) {
        //테스트 편의를 위한 설정. 로그인 이후 필요 없을시 삭제
        User usertmp = userRepository.getByUserId(userId);
        List<MessageBox> messages = messageBoxRepository.findByUserIdOrderByIsCheckedAscCreateTimeDesc(usertmp);
        //List<MessageBox> messages = messageBoxRepository.findByUserIdOrderByIsCheckedAscCreateTimeDesc(userId);
        List<MyMessageBoxDTO> MyMessageDtos = new ArrayList<>();
        for (MessageBox message : messages) {
            MyMessageDtos.add(MyMessageBoxDTO.builder()
                    .messageId(message.getMessageId())
                    .content(message.getContent())
                    .isChecked(message.getIsChecked())
                    .createTime(message.getCreateTime())
                    .build());
        }
        return MyMessageDtos;
    }

    @Override
    @Transactional
    public void markAsCheckedByMessageId(Long messageId) {

        if (messageId == null) {
            throw new IllegalArgumentException("messageId cannot be null");
        }

        Optional<MessageBox> messageBoxOptional = messageBoxRepository.findById(messageId);
        if (messageBoxOptional.isEmpty()) {
            throw new RuntimeException("No message found for the given messageId");
        }

        messageBoxRepository.markAsCheckedById(messageId);
    }

    @Override
    @Transactional
    public void markAsCheckedAll(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }

        User user = userRepository.getByUserId(userId);
        List<MessageBox> messageBoxList = messageBoxRepository.findByUserIdOrderByIsCheckedAscCreateTimeDesc(user);
//        if (messageBoxList == null || messageBoxList.isEmpty()) {
//            throw new RuntimeException("No messages found for the user");
//        }
//        messageBoxRepository.markAsCheckedByUser(userId);

        if (messageBoxList == null || messageBoxList.isEmpty()) {
            throw new RuntimeException("No messages found for the user");
        }

        messageBoxRepository.markAsCheckedByUser(userId);

//        for (MessageBox messageBox : messageBoxList) {
//            messageBox.setIsChecked(true);
//        }
//
//        messageBoxRepository.saveAll(messageBoxList);
    }

    @Override
    @Transactional
    public void RemoveMessage(Long messageId) {
        messageBoxRepository.deleteByMessageId(messageId);
    }


}
