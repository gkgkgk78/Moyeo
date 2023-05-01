package com.moyeo.main.config.security;

import com.moyeo.main.exception.BaseException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws BaseException, IOException {
        response.sendRedirect("http://localhost:9999/exception/accessDenied");
    }
}
