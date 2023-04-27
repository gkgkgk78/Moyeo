package com.moyeo.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class AddPostReq {
    @NotNull(message = "[Request] timelineId 값을 입력해주세요.")
    private Long timelineId;
    @NotNull(message = "[Request] address1 값을 입력해주세요.")
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    }
