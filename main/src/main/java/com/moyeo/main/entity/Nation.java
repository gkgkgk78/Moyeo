package com.moyeo.main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Nation {

    /*
     *  nation은 s3와 연관을 지어서 찾아내고자 합니다
     * -front로부터 국기와 국가이름, 도로명 주소를 넘겨 받으면
     * -넘겨받은 country code 를 바탕으로 지금까지 한번이라도 해당 국기가 s3에 저장이 되었는지를 찾아봅니다
     * -if)찾아보았는데 존재 하지 않다면 front로부터 넘겨받은 국기를 s3에 저장하여 주는 과정이 필요합니다
     * -elif)찾아보았는데 존재 한다면 s3 로부터 이미지를 넘겨받아 post select시 넘겨줍니다
     *
     * */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "TINYINT")
    private Integer nationId;
    @Column(length = 60)
    private String name;

    @Column(length = 120)
    private String nationUrl;

}



