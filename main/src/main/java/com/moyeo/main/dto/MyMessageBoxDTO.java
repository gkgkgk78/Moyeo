package com.moyeo.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyMessageBoxDTO {

    private Long messageId;
    private String content;
    private Boolean isChecked;
    private LocalDateTime createTime;

}
