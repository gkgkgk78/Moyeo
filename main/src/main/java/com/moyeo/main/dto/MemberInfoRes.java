package com.moyeo.main.dto;

import com.moyeo.main.entity.MoyeoPublic;
import com.moyeo.main.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoRes {
	private Long userId;
	private String clientId;
	private String nickname;
	private String profileImageUrl;
	@Builder
	public MemberInfoRes(MoyeoPublic moyeoPublic) {
		User user = moyeoPublic.getUserId();

		this.userId = user.getUserId();
		this.clientId = user.getClientId();
		this.nickname = user.getNickname();
		this.profileImageUrl = user.getProfileImageUrl();
	}

	@Builder
	public MemberInfoRes(User user) {
		this.userId = user.getUserId();
		this.clientId = user.getClientId();
		this.nickname = user.getNickname();
		this.profileImageUrl = user.getProfileImageUrl();
	}


}
