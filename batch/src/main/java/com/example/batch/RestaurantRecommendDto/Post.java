package com.example.batch.RestaurantRecommendDto;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post {
    @Id
    Long id;
    String title;

}
