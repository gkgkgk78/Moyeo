package com.moyeo.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyeo.main.entity.User;
import com.moyeo.main.service.MoyeoTimeLineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/auth/moyeo/timeline")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "MoyeoTimeline", description = "모여 시작 기능 (모여 타임라인 생성)")
public class MoyeoTimelineController {
	private final MoyeoTimeLineService moyeoTimeLineService;
	@PostMapping
	@Operation(summary = "동행 시작", description = "동행 여행 시작 -> 동행 타임라인 생성")
	public ResponseEntity<?> registMoyeoTimeline() throws Exception {
		log.info("동행 타임라인 생성 시작");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if (auth != null && auth.getPrincipal() != null)
			user = (User) auth.getPrincipal();

		return ResponseEntity.ok(moyeoTimeLineService.registMoyeoTimeline(user));
	}
}
