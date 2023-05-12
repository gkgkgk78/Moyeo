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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/auth/moyeo/members")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "MoyeoMembers", description = "모여 멤버 초대, 참여, 나가기 기능")
public class MoyeoMembersController {
	private final MoyeoMembersService moyeoMembersService;

	@PostMapping("/invite")
	@Operation(summary = "동행 초대, 푸시 알림 보내기 & 메시지 함에 저장")
	public ResponseEntity<?> inviteMoyeoMembers(@RequestBody MoyeoMembersReq moyeoMembersReq) throws Exception {
		log.info("동행 초대 시작...");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if (auth != null && auth.getPrincipal() != null) {
			user = (User) auth.getPrincipal();
		}

		return ResponseEntity.ok(moyeoMembersService.inviteMoyeoMembers(user, moyeoMembersReq));
	}

	@PostMapping
	@Operation(summary = "동행 참여")
	public ResponseEntity<?> registMoyeoMembers(@RequestBody MoyeoMembersReq moyeoTimelineId) throws Exception {
		log.info("동행 참여 시작...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if (auth != null && auth.getPrincipal() != null)
			user = (User) auth.getPrincipal();

		return ResponseEntity.ok(moyeoMembersService.registMoyeoMembers(user, moyeoTimelineId.getMoyeoTimelineId()));
	}

	@PutMapping
	@Operation(summary = "동행 나가기")
	public ResponseEntity<?> updateMoyeoMembers(@RequestBody MoyeoMembersReq moyeoTimelineId) throws Exception {
		log.info("동행 나가기 시작...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if (auth != null && auth.getPrincipal() != null)
			user = (User) auth.getPrincipal();

		return ResponseEntity.ok(moyeoMembersService.updateMoyeoMembers(user, moyeoTimelineId.getMoyeoTimelineId()));
	}

}
