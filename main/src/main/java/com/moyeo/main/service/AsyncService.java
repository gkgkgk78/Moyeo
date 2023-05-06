package com.moyeo.main.service;

import com.moyeo.main.dto.PostInsertReq;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AsyncService {

    @Autowired
    private final OkHttpClient okHttpClient;

    @Async("sampleExecutor")
    public CompletableFuture<String> testAsync(String message) {

        for (int i = 1; i <= 3; i++) {
            System.out.println(message + "비동기 : " + i);
        }

        if (message == "2") {
            throw new RuntimeException();
        }

        return CompletableFuture.completedFuture("성공" + message);
    }

    //이제 notification 서버에 접근하여 띄우는 작업이 필요로 한다.
    //notification을 하려면...
    //해당 되는 유저의 토큰과 검색 하고자 하는 위치를 기반으로 gpt에게 날릴거임

    //notification서버에 보내는 작업을 진행하게 될 메서드를 의미를 함
    @Async("sampleExecutor")
    public void toNotification(PostInsertReq post) {

        FormBody.Builder formBuilder = new FormBody.Builder();
        formBuilder.add("userId", post.getUserId());//여기에 key, value 매핑하여 넣어줘야할 데이터 넣어주면됨 (위치, 사용자 이름, 토큰)
        formBuilder.add("address1", post.getAddress1());
        formBuilder.add("address2", post.getAddress2());
        formBuilder.add("address3", post.getAddress3());
        formBuilder.add("address4", post.getAddress4());
        formBuilder.add("deviceToken", post.getDeviceToken());

        Request request = new Request.Builder()
                .url("http://localhost:9000/autogpt")
                .addHeader("Content-Type", "application/json;")
                .addHeader("Cache-Control", "no-cache")
                .post(formBuilder.build())
                .build();
        System.out.println("현재 notification 실행이 됨");

        try {
            okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            System.out.println("문제 발생");
            throw new BaseException(ErrorMessage.ASYNC_RUN_ERROR);
        }


    }

}
