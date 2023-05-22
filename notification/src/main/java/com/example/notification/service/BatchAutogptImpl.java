package com.example.notification.service;


import com.example.sender.DTO.BatchMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchAutogptImpl implements BatchAutogpt{
    @Value("${flask-url}")
    private String autogpt;
    private final FcmService fcmService;
    private final MessageBoxService messageBoxService;
    @Override
    public void insert(BatchMessage message) throws Exception {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
        String goal = "Search for a good restaurant near " + message.getAddress1() + " " + message.getAddress2() + " " + message.getAddress3()+" "+message.getAddress4() +".";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Title",message.getTitle());
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = new HashMap<>();
        map.put("data",goal);

        HttpEntity<String> autoGptEntity = new HttpEntity<String>(mapper.writeValueAsString(map), headers);
        ResponseEntity<String> response = restTemplate.exchange(autogpt, HttpMethod.POST, autoGptEntity, String.class);
        Map<String, String> responseMap = mapper.readValue(response.getBody(), Map.class);
        log.info("Autogpt result : {}",responseMap.get("result"));


//        String result = fcmService.pushNoti(message.getDeviceToken(), responseMap.get("result"));

        messageBoxService.insert(message.getUserId()+"",responseMap.get("result"));

    }
}
