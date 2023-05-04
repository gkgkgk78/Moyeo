package com.moyeo.main.conponent;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public ResponseEntity<String> sendYeobotData(String caseType, String data) throws IOException {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, data);
        Request request = new Request.Builder()
                .header("title", caseType)
                .url(flaskUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        //response.code가 200이 아닐경우 처리를 해야함
        String responseBody = response.body().string();
        Gson gson = new Gson();
        Map<String, String> getdata = gson.fromJson(responseBody, Map.class);
        String result = getdata.get("result");

        return ResponseEntity.ok(responseBody);
    }

}