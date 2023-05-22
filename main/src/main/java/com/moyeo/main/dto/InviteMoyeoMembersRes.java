package com.moyeo.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class InviteMoyeoMembersRes {
	private int totalInviteCount;
	private int successInviteCount;
}
