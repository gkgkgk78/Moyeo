package com.danim.exception.controller;

import com.danim.config.security.common.CommonResponse;
import com.danim.exception.BaseException;
import com.danim.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/exception")
public class ExceptionController {
    //401
    @GetMapping("/entryPoint")
    public BaseException entryPointException() {
        System.out.println("들어옴 1");
        throw new BaseException(ErrorMessage.UNAUTHORIZED_ACCESSTOKEN);
    }
    //403
    @GetMapping("/accessDenied")
    public BaseException accessDeniedException() {
        System.out.println("들어옴 2");
        throw new BaseException(ErrorMessage.NOT_PERMISSION_EXCEPTION);
    }
}
