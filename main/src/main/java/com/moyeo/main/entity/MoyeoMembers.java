package com.moyeo.main.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
// @IdClass(MoyeoMembersID.class)
@EntityListeners(AuditingEntityListener.class)
public class MoyeoMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moyeoMembersId;

    // @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User userId;

    private Long moyeoTimelineId;
    @CreatedDate
    private LocalDateTime joinTime;
    private LocalDateTime finishTime;

    public void setMoyeoMembers(User userId, Long moyeoTimelineId) {
        this.userId = userId;
        this.moyeoTimelineId = moyeoTimelineId;
    }

}
