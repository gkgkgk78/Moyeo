package com.moyeo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    private Post postId;

    @Column(length = 120, unique = true, nullable = false)
    private String photoUrl;


    // @Builder.Default
    // @ColumnDefault("0")
    // private Boolean isLive = false;

}
