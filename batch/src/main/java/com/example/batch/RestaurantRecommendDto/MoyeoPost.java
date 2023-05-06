package com.example.batch.RestaurantRecommendDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MoyeoPost{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moyeoPostId;

    private String address1;

    private String address2;

    private String address3;

    private String address4;

    private String createTime;


    @ManyToOne
    @JoinColumn(name="moyeo_timeline_id")
//    @ToString.Exclude
    private MoyeoTimeLine moyeoTimeline;


}
