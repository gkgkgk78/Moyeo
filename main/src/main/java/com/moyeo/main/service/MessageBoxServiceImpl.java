package com.moyeo.main.service;

import com.moyeo.main.dto.MyMessageBoxDTO;
import com.moyeo.main.entity.MessageBox;
import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import com.moyeo.main.repository.MessageBoxRepository;
import com.moyeo.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBoxServiceImpl implements MessageBoxService {

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
                    .inviteKey(message.getInviteKey())
                    .build());
        }
        return MyMessageDtos;
    }

    @Override
    @Transactional
    public void markAsCheckedByMessageId(Long messageId) {

        messageBoxRepository.markAsCheckedById(messageId);
    }

    @Override
    @Transactional
    public void markAsCheckedAll(Long userId) {

        User user = userRepository.getByUserId(userId);
        List<MessageBox> messageBoxList = messageBoxRepository.findByUserIdOrderByIsCheckedAscCreateTimeDesc(user);

        if (messageBoxList == null || messageBoxList.isEmpty()) {
            throw new BaseException(ErrorMessage.NO_MESSAGE_FOR_USER);
        }

        messageBoxRepository.markAsCheckedByUser(userId);

    }

    @Override
    @Transactional
    public void RemoveMessage(Long messageId) {
        messageBoxRepository.deleteByMessageId(messageId);
    }


    @Override
    public void insertMessage(Long userId, String content) {
        User user = userRepository.getByUserId(userId);
        LocalDateTime createTime = LocalDateTime.now();

        MessageBox messageBox = MessageBox.builder()
                .userId(user)
                .content(content)
                .createTime(createTime)
                .build();
        messageBoxRepository.save(messageBox);
    }

}
