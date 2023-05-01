package com.moyeo.main.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.moyeo.main.id.MoyeoMembersID;

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
@Builder
@IdClass(MoyeoMembersID.class)
public class MoyeoMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moyeoMembersId;

    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User userId;

    private Long moyeoTimelineId;
    private LocalDateTime joinTime;
    private LocalDateTime finishTime;

}
