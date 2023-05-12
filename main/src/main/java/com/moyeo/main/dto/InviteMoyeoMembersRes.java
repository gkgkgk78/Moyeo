package com.moyeo.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class InviteMoyeoMembersRes {
	private int totalInviteCount;
	private int successInviteCount;
}
