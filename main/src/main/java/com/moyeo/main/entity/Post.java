package com.moyeo.main.entity;

import lombok.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@DynamicInsert
public class Post extends BaseTime{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "post_id", nullable = false)
	private Long postId;

	@Column(length = 50)
	private String address1;
	@Column(length = 50)
	private String address2;
	@Column(length = 50)
	private String address3;
	@Column(length = 50)
	private String address4;

	@OneToMany(mappedBy = "postId",fetch = FetchType.EAGER)
	@Builder.Default
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Photo> photoList = new ArrayList<>(); // photoId 리스트

	@Column(length = 100)
	private String text;
	private Double voiceLength;
	@Column(length = 120)
	private String voiceUrl;

	// private String nationUrl;

	// Post 테이블과 Nation 테이블 FK
	@ManyToOne
	@JoinColumn(name="nation_id")
	@ToString.Exclude
	private Nation nationId;

	// Post 테이블과 TimeLine 테이블 FK
	@ManyToOne
	@JoinColumn(name="timeline_id")
	@ToString.Exclude
	@OnDelete(action = OnDeleteAction.CASCADE)
	private TimeLine timelineId;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User userId;

	@Column(columnDefinition = "MEDIUMINT")
	@ColumnDefault("0")
	private Long favoriteCount;

	// private = null;

	public void updateFavoriteCount(Integer amount) {
		this.favoriteCount += amount;
		if (this.favoriteCount < 0) {
			this.favoriteCount = 0L;
		}
	}

}