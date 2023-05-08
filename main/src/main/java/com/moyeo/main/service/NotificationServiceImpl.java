package com.moyeo.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {

    @Async("sampleExecutor")
    public ListenableFuture<String> testAsync(String message) {
        
        //이제 여기서 notification서버로 요청을 보내 보도록 하자

        
        
        
        return new AsyncResult<>("성공" + message);
    }
}