package com.example.batch.RestaurantRecommendDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MoyeoMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moyeoMembersId;
    @ManyToOne
    @JoinColumn(name="moyeo_timeline_id")
    private MoyeoTimeLine moyeoTimelineId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")

    private User user;

}