package com.moyeo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChangeFavoriteStatusRes {
    private Long postId;
    private boolean isFavorite;
    private Long TotalFavorite;
}