package com.example.notification.component;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class YeobotClient {
    private final OkHttpClient okHttpClient;
    private final String flaskUrl;

    public YeobotClient(OkHttpClient okHttpClient, @Value("${flask-url}") String flaskUrl) {
        this.okHttpClient = okHttpClient;
        this.flaskUrl = flaskUrl;
    }

    public String sendYeobotData(String caseType, String data) throws Exception {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, data);
        Request request = new Request.Builder()
                .header("title", caseType)
                .url(flaskUrl)
                .post(requestBody)
                .build();

        return "테스트중";
//        Response response = null;
//        String result="";
//        try {
//            response = okHttpClient.newCall(request).execute();
//        } catch (Exception e) {
//            log.info("flask 서버 에러");
//            throw new Exception(e.getMessage());
//        }
//        String responseBody = null;
//        try {
//            responseBody = response.body().string();
//            Gson gson = new Gson();
//            Map<String, String> getdata = gson.fromJson(responseBody, Map.class);
//            result = getdata.get("result");
//        } catch (Exception e) {
//            log.info("메시지가 너무 길었다");
//            throw new Exception(e.getMessage());
//        }
//        return result;
    }

}