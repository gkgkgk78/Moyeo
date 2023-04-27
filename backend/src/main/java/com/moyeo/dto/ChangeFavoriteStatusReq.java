package com.moyeo.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChangeFavoriteStatusReq {
//        @NotNull(message = "[Request] userUid 값을 입력해주세요.")
//        private Long userUid;
        @NotNull(message = "[Request] postId 값을 입력해주세요.")
        private Long postId;
}
