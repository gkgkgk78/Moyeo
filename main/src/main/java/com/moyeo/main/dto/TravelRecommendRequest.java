package com.moyeo.main.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelRecommendRequest {

    private String destination;
    private String season;
    private String purpose;

}
