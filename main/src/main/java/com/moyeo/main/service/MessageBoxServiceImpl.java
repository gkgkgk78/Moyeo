package com.moyeo.main.service;

import com.moyeo.main.repository.MessageBoxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageBoxServiceImpl implements MessageBoxService{

    private final MessageBoxRepository messageBoxRepository;

}
