package com.moyeo.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class VoiceTextResult {
	private String voiceUrl;
	private Double durationInSeconds;
	private String text;
}
