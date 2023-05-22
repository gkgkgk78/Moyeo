package com.moyeo.main.conponent;

import com.moyeo.main.dto.WordInfo;
import com.google.gson.Gson;
//import com.squareup.okhttp.*;
import okhttp3.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ConnectException;
import java.util.List;

@Component
@Slf4j
public class Http {
    public static final MediaType MEDIA_TYPE = MediaType.parseMediaType("audio/wav");

    private final OkHttpClient client = new OkHttpClient();

    public File Post(String toUrl, String method, List<WordInfo> words, File voice) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("words", gson.toJson(words))

//                .addFormDataPart("voice",new File("test.wav")))
                .addFormDataPart("file", "voicefile", RequestBody.create(okhttp3.MediaType.parse("audio/wav"), voice))
                .build();


        Request request = new Request.Builder()
                .url(toUrl)
                .post(formBody)
                .build();
        Response response = client.newCall(request).execute();
        log.info("response info :{}",response);
        if (!response.isSuccessful()) throw new ConnectException();
            // 응답이 성공적으로 받아진 경우
        ResponseBody responseBody = response.body();
//        if (responseBody == null) throw new NullPointerException();
            // 응답 본문을 읽어옴
        InputStream inputStream = responseBody.byteStream();
        // 'audio' 처리 로직 구현
        File file = new File("haha.wav");

//        byte[] bytes = StreamUtils.copyToByteArray(inputStream);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        // InputStream에서 파일로 데이터 복사
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
        // 스트림과 연결된 자원 해제
        fileOutputStream.flush();
        fileOutputStream.close();
        inputStream.close();
        log.info("file info : {}",file);

        return file;
    }
}
