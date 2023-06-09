package com.moyeo.main.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChangeFavoriteStatusRes {
    private Long postId;
    private boolean isMoyeo;
    private boolean isFavorite;
    private Long TotalFavorite;
}