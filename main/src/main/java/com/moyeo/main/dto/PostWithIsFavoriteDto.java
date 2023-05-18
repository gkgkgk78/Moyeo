package com.moyeo.main.dto;

import com.moyeo.main.entity.Post;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PostWithIsFavoriteDto {
	private Post post;
	private Boolean isFavorite;

	public PostWithIsFavoriteDto(Post post, Boolean isFavorite) {
		this.post = post;
		this.isFavorite = isFavorite;
	}

}
