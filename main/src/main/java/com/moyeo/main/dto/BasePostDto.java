package com.moyeo.main.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Nation;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder(builderMethodName = "BasePostDtoBuilder")
@Getter
public class BasePostDto {
	private Long postId;
	private LocalDateTime createTime;
	private Nation nationId;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	// private List<Photo> photoList = new ArrayList<>(); // photoId 리스트
	private List<String> photoList; // photoId 리스트
	private String text;
	private Double voiceLength;
	private String voiceUrl;
	// private TimeLine timelineId;
	// private User userId;
	private Long favoriteCount;


	private Boolean isFavorite;
	private Boolean isMoyeo;
	private List<PostMembers> members;

	public static BasePostDto.BasePostDtoBuilder builder(Post entity, Boolean isFavorite) {
		List<String> photos = new ArrayList<>();
		for (Photo photo : entity.getPhotoList()) {
			photos.add(photo.getPhotoUrl());
		}

		return BasePostDtoBuilder()
			.postId(entity.getPostId())
			.createTime(entity.getCreateTime())
			.nationId(entity.getNationId())
			.address1(entity.getAddress1())
			.address2(entity.getAddress2())
			.address3(entity.getAddress3())
			.address4(entity.getAddress4())
			.photoList(photos)
			.text(entity.getText())
			.voiceLength(entity.getVoiceLength())
			.voiceUrl(entity.getVoiceUrl())
			// .timelineId(entity.getTimelineId())
			// .userId(entity)
			.favoriteCount(entity.getFavoriteCount())
			.isFavorite(isFavorite)
			.isMoyeo(false)
			.members(null)
			;
	}

	public static BasePostDto.BasePostDtoBuilder builder(MoyeoPost entity, Boolean isFavorite, List<PostMembers> members) {
		List<String> photos = new ArrayList<>();
		for (MoyeoPhoto photo : entity.getMoyeoPhotoList()) {
			photos.add(photo.getPhotoUrl());
		}

		return BasePostDtoBuilder()
			.postId(entity.getMoyeoPostId())
			.createTime(entity.getCreateTime())
			.nationId(entity.getNationId())
			.address1(entity.getAddress1())
			.address2(entity.getAddress2())
			.address3(entity.getAddress3())
			.address4(entity.getAddress4())
			.photoList(photos)
			.text(entity.getText())
			.voiceLength(entity.getVoiceLength())
			.voiceUrl(entity.getVoiceUrl())
			.favoriteCount(entity.getFavoriteCount())
			.isFavorite(isFavorite)
			.isMoyeo(true)
			.members(members)
			;
	}

}
