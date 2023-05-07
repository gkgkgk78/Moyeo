package com.example.notification.service;



public interface FcmService {

    public void send(String token,String content) throws Exception;


}
