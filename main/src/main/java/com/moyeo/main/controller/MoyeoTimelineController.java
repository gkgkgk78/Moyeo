package com.moyeo.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyeo.main.dto.registMoyeoTimelineReq;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/auth/moyeo/timeline")
@RequiredArgsConstructor
@Log4j2
public class MoyeoTimelineController {

	@PostMapping
	public ResponseEntity<?> registMoyeoTimeline(@RequestBody List<registMoyeoTimelineReq> members) throws Exception {

		return ResponseEntity.ok().build();
	}
}
