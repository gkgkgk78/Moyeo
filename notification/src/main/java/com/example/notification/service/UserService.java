package com.example.notification.service;

public interface UserService {
    Long findByDeviceToken(String deviceToken);
}
