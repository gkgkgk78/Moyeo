package com.moyeo.main.controller;

import com.moyeo.main.service.MessageBoxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth/message")
@RestController
@Log4j2
public class MessageBoxController {

    private final MessageBoxService messageBoxService;

}
