package com.moyeo.main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyeo.main.dto.MoyeoMembersReq;
import com.moyeo.main.entity.User;
import com.moyeo.main.repository.UserRepository;
import com.moyeo.main.service.MoyeoMembersService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/auth/moyeo/members")
@RequiredArgsConstructor
@Log4j2
public class MoyeoMembersController {
	private final MoyeoMembersService moyeoMembersService;
	@PostMapping
	public ResponseEntity<?> registMoyeoMembers(@RequestBody MoyeoMembersReq moyeoTimelineId) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if (auth != null && auth.getPrincipal() != null)
			user = (User) auth.getPrincipal();

		return ResponseEntity.ok(moyeoMembersService.registMoyeoMembers(user, moyeoTimelineId.getMoyeoTimelineId()));
	}

	@PutMapping
	public ResponseEntity<?> updateMoyeoMembers(@RequestBody MoyeoMembersReq moyeoTimelineId) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if (auth != null && auth.getPrincipal() != null)
			user = (User) auth.getPrincipal();

		return ResponseEntity.ok(moyeoMembersService.updateMoyeoMembers(user, moyeoTimelineId.getMoyeoTimelineId()));
	}
}
