package com.example.notification.service;

import com.example.notification.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    @Override
    public Long findByDeviceToken(String deviceToken) {

        return userRepository.findByDeviceToken(deviceToken).getUserId();
    }
}
