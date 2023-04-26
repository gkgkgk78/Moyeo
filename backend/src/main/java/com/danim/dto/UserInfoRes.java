package com.danim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoRes {
    private Long userUid;
    private String nickname;
    private String profileImageUrl;
    private Long timeLineId;
    private Integer timelineNum;
}
