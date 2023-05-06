package com.moyeo.main.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistMoyeoRes {
	private Long timelineId;
	private Long moyeoTimelineId;
	private Long userId;
}
