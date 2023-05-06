package com.moyeo.main.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @ColumnDefault("0")
    private Long membersCount;

    public void updateMembersCount(Integer amount) {
        this.membersCount += amount;
        if (this.membersCount < 0) {
            this.membersCount = 0L;
        }
    }

}

