package com.example.notification.dto;

import lombok.*;


@Data
@NoArgsConstructor
@ToString
public class BatchMessage {
    private String Title;
    private Long userId;
    private String deviceToken;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
}