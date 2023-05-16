package com.example.notification.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Builder
@Data
public class BatchMessage {
//    private static final long serialVersionUID = 2349683458261238690L;
    private long id;
    private String message;
    private String deviceToken;

}
