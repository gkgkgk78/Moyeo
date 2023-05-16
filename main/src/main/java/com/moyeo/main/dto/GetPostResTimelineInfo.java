package com.moyeo.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GetPostResTimelineInfo {
	private Long timelineId;
	private String userNickname;
	private String userProfileImageUrl;
}
