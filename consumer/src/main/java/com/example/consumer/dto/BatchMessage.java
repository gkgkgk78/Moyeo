package com.example.consumer.dto;

import lombok.*;

import java.io.Serializable;


@NoArgsConstructor
@Data
public class BatchMessage {
//    private static final long serialVersionUID = 2349683458261238690L;
    private long id;
    private String message;
    private String deviceToken;

}

