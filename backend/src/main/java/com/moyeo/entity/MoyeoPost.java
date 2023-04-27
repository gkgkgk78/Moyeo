package com.moyeo.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@DynamicInsert
public class MoyeoPost extends BaseTime{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "moyeo_post_id", nullable = false)
	private Long moyeoPostId;

	@Column(length = 50)
	private String address1;
	@Column(length = 50)
	private String address2;
	@Column(length = 50)
	private String address3;
	@Column(length = 50)
	private String address4;

	@OneToMany(mappedBy = "moyeoPostId",fetch = FetchType.EAGER)
	@Builder.Default
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<MoyeoPhoto> moyeoPhotoList = new ArrayList<>();

	@Column(length = 100)
	private String text;
	private Double voiceLength;
	@Column(length = 120)
	private String voiceUrl;

	// Post 테이블과 Nation 테이블 FK
	@ManyToOne
	@JoinColumn(name="nation_id")
	@ToString.Exclude
	private Nation nationId;

	// Post 테이블과 TimeLine 테이블 FK
	@ManyToOne
	@JoinColumn(name="moyeo_timeline_id")
	@ToString.Exclude
	@OnDelete(action = OnDeleteAction.CASCADE)
	private MoyeoTimeLine moyeoTimelineId;

	@Column(columnDefinition = "MEDIUMINT")
	@ColumnDefault("0")
	private Long favoriteCount;

}