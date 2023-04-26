package com.danim.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photoId", nullable = false)
    private Long photoId;

    @Column(length = 100, unique = true, nullable = false)
    private String photoUrl;


    // @Builder.Default
    // @ColumnDefault("0")
    // private Boolean isLive = false;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post postId;
}
