package com.moyeo.main.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.LastModifiedDate;

import com.moyeo.main.entity.Nation;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;

import lombok.Builder;
import lombok.ToString;

public class GetFavoritePostListRes {
	private Long postId;
	private LocalDateTime createTime;
	private LocalDateTime modifyTime;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private Long favoriteCount;
	private List<Photo> photoList = new ArrayList<>();
	private String text;
	private Double voiceLength;
	private String voiceUrl;
	private Nation nationId;
	private TimeLine timelineId;
	private User userId;
	private Boolean isMoyeo;
}
