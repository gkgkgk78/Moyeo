package com.example.consumer.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

public class PostInsertReq {

    private String deviceToken;
    private String address1;//위치 정보
    private String address2;
    private String address3;
    private String address4;
    private Long userId;//고유한 사용자 아이디


}
