package com.moyeo.id;

import java.io.Serializable;

import com.moyeo.entity.Post;
import com.moyeo.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // equals, hashCode 생성용 Lombok Annotation
@NoArgsConstructor  // 기본 생성자 생성용 Lombok Annotation
@AllArgsConstructor
public class FavoriteID implements Serializable {
	private Post postId;
	private User userId;

}
