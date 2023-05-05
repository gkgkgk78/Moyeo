package com.moyeo.main.service;

import com.moyeo.main.dto.MyMessageBoxDTO;
import com.moyeo.main.entity.MessageBox;
import com.moyeo.main.repository.MessageBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBoxServiceImpl implements MessageBoxService{

    private final MessageBoxRepository messageBoxRepository;

    public List<MyMessageBoxDTO> getMessagesByUser(Long userId) {
        List<MessageBox> messages = messageBoxRepository.findByUserIdOrderByIsCheckedAscCreateTimeDesc(userId);
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

    @Transactional
    public void markAsCheckedByMessageId(Long messageId) {
        messageBoxRepository.markAsCheckedById(messageId);
    }

    @Override
    public void markAsCheckedAll(Long userId) {
        messageBoxRepository.markAsCheckedByUser(userId);
    }

    @Override
    public void RemoveMessage(Long messageId) {
        messageBoxRepository.deleteByMessageId(messageId);
    }


}
