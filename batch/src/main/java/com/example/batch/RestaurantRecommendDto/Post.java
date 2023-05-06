package com.example.batch.RestaurantRecommendDto;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {
    @Id
    Long postId;
    String address1;
    String address2;
    String address3;
    String address4;
    private String createTime;//생성시간
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
