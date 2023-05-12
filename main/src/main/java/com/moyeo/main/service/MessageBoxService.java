package com.moyeo.main.service;

import com.moyeo.main.dto.MyMessageBoxDTO;

import java.util.List;

public interface MessageBoxService {

    public List<MyMessageBoxDTO> getMessagesByUser(Long userId);

    public void markAsCheckedByMessageId(Long messageId);

    public void markAsCheckedAll(Long userId);

    public void RemoveMessage(Long messageId);

    public void insertMessage(Long userId, String content);


}
