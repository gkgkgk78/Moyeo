package com.moyeo.main.dto;

import java.util.ArrayList;
import java.util.List;

import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostMembers {
	private Long userId;
	private String clientId;
	private String nickname;
	private String profileImageUrl;
	@Builder
	public PostMembers(MoyeoPublic moyeoPublic) {
		User user = moyeoPublic.getUserId();

		this.userId = user.getUserId();
		this.clientId = user.getClientId();
		this.nickname = user.getNickname();
		this.profileImageUrl = user.getProfileImageUrl();
	}


}
