package com.moyeo.entity;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

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
@DynamicInsert //@DynamicInsert사용
@Builder

public class MoyeoTimeLine extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moyeoTimelineId;

    @Column(name = "title", length = 100, nullable = false)
    @Builder.Default
    @ColumnDefault("'동행 중'")
    private String title = "동행 중";

    @Builder.Default
    @ColumnDefault("0")
    private Boolean isComplete = false;

    private LocalDateTime finishTime;

    @Builder.Default
    @ColumnDefault("1")
    private Boolean isTimelinePublic = true;

    @Column(columnDefinition = "MEDIUMINT")
    private Integer membersCount;

}

