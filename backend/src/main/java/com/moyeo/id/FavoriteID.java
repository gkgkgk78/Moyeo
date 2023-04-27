package com.moyeo.id;

import java.io.Serializable;

import com.moyeo.entity.Post;
import com.moyeo.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteID implements Serializable {
	private Long postId;
	private Long userId;

}
