package com.example.sender.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


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
