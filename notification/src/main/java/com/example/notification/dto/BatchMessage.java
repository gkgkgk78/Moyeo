package com.example.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchMessage {
    private String Title;
    private Long userId;
    private String deviceToken;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
}
