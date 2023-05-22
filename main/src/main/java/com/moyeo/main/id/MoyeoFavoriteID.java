package com.moyeo.main.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoyeoFavoriteID implements Serializable {
	private Long moyeoPostId;
	private Long userId;

}
