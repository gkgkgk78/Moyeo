package com.example.notification.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userId;

    @Column(length = 30, unique = true, nullable = false)
    private String clientId;

    @Column(length = 30)
    private String nickname;

    @Column(length = 100)
    private String password;
    @Column(length = 100)
    private String profileImageUrl;

    @Column
    private String refreshToken;

    @Column(length = 200)
    private String deviceToken;

    //@ApiModelProperty(hidden = true)
    private String role;


}
