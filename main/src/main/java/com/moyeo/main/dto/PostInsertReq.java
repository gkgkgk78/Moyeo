package com.moyeo.main.dto;

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
    private String location;//위치 정보 
    private String userId;//고유한 사용자 아이디


}
