package com.moyeo.main.dto;

import java.time.LocalDateTime;

public interface GetTimelineListRes {
	Long getTimelineId();
	String getTitle();
	Long getUserId();
	String getNickname();
	String getPhotoUrl();
	LocalDateTime getStartTime();
	String getStartPlace();
	LocalDateTime getLastTime();
	String getLastPlace();
}
