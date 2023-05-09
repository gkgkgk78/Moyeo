package com.example.batch.RestaurantRecommendDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PushTable {
    @Id
    private String deviceToken;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
}
