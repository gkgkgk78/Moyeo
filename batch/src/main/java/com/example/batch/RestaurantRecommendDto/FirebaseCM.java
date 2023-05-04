package com.example.batch.RestaurantRecommendDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "FirebaseCM")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class FirebaseCM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String message;
}
