package com.moyeo.main.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data               // equals, hashCode 생성용 Lombok Annotation
@NoArgsConstructor  // 기본 생성자 생성용 Lombok Annotation
@AllArgsConstructor
public class MoyeoFavoriteID implements Serializable {
	private Long moyeoPostId;
	private Long userId;

}
