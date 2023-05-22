package com.moyeo.main.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimelinePostInner {

    private String flag;
    private String nation;

    private String startDate;
    private String finishDate;

    private List<MyPostDtoRes> postList;

}
