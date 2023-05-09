package com.example.notification.entity;


import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder(builderMethodName = "ChatBuilder")
public class Chat {

    private String message;
    private String sender;

    @CreatedDate
    private LocalDateTime createTime;//생성시간

    public static ChatBuilder builder(String content )
    {

        return ChatBuilder()
                .message(content)
                .sender("gpt")
                .createTime(LocalDateTime.now());
    }


}
