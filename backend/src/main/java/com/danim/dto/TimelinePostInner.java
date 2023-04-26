package com.danim.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimelinePostInner {


    private String flag;
    private String nation;
//    private Boolean isMine;//해당 포스트가 내것인지 여부를 파악 하기 위한 변수

    private String startDate;
    private String finishDate;

    private List<MyPostDtoRes> postList;

}
