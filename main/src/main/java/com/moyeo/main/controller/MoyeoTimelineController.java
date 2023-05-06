package com.moyeo.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyeo.main.entity.User;
import com.moyeo.main.service.MoyeoTimeLineService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/auth/moyeo/timeline")
@RequiredArgsConstructor
@Log4j2
public class MoyeoTimelineController {
	private final MoyeoTimeLineService moyeoTimeLineService;
	@PostMapping
	public ResponseEntity<?> registMoyeoTimeline() throws Exception { // 동행 시작
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if (auth != null && auth.getPrincipal() != null)
			user = (User) auth.getPrincipal();

		return ResponseEntity.ok(moyeoTimeLineService.registMoyeoTimeline(user));
	}
}
