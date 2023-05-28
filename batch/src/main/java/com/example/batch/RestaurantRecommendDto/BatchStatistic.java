package com.example.batch.RestaurantRecommendDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class BatchStatistic {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long batchStatisticId;
    private String deviceToken;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    @Builder
    public BatchStatistic(String deviceToken, String address1,String address2,String address3,String address4){
        this.deviceToken = deviceToken;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
    }
    public BatchStatistic ConvertToBatchStatistic(PushTable pushTable){
        return BatchStatistic.builder().
                deviceToken(pushTable.getDeviceToken()).
                address1(pushTable.getAddress1()).
                address2(pushTable.getAddress2()).
                address3(pushTable.getAddress3()).
                address4(pushTable.getAddress4()).
                build();
    }
}
