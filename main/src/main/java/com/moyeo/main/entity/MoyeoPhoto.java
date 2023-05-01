package com.moyeo.main.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class MoyeoPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moyeoPhotoId;

    @ManyToOne
    @JoinColumn(name = "moyeo_post_id")
    @JsonIgnore
    private MoyeoPost moyeoPostId;

    @Column(length = 120, unique = true, nullable = false)
    private String photoUrl;

}
