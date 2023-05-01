package com.moyeo.main.config.security.common;

import com.moyeo.main.exception.BaseException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws BaseException, IOException {
        response.sendRedirect("http://localhost:9999/exception/entryPoint");
    }
}
