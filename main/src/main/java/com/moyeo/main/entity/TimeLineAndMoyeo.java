package com.moyeo.main.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
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
public class TimeLineAndMoyeo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moyeoId;

    @ManyToOne
    @JoinColumn(name="timeline_id")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TimeLine timelineId;

    @ManyToOne
    @JoinColumn(name="moyeo_timeline_id")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MoyeoTimeLine moyeoTimelineId;

    @ColumnDefault("0")
    private Long lastPostOrderNumber;

}
