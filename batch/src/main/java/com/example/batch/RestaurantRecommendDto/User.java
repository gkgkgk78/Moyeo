package com.example.batch.RestaurantRecommendDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {
    @Id
    Long userId;
    String deviceToken;
//    @OneToMany(mappedBy = "user")
//    @Builder.Default
//    private List<MoyeoMembers> moyeoMembersList = new ArrayList<>();
}
