package com.moyeo.main.dto;


import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class ChatReq {

    private String message;
    private String sender;




}
