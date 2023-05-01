package com.moyeo.main.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.moyeo.main.id.MoyeoPublicID;

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
@IdClass(MoyeoPublicID.class)
public class MoyeoPublic {
    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User userId;
    @Id
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "moyeo_post_id")
    private MoyeoPost moyeoPostId;

    @Builder.Default
    @ColumnDefault("1")
    private Boolean isPublic = true;

    @Builder.Default
    @ColumnDefault("0")
    private Boolean isDeleted = false;

    private LocalDateTime createTime;

}
