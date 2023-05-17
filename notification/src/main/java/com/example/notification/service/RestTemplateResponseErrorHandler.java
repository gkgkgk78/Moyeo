package com.example.notification.service;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return true;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if(response.getStatusCode()== HttpStatus.INTERNAL_SERVER_ERROR) throw new CustomException("Auto gpt 결과 에러");
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ResponseErrorHandler.super.handleError(url, method, response);
    }
}
