package com.moyeo.dto;

import com.moyeo.entity.Photo;
import com.moyeo.entity.Post;
import com.moyeo.entity.TimeLine;
import com.moyeo.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder(builderMethodName = "MainTimelinePhotoDtoResBuilder")
@Getter
public class MainTimelinePhotoDtoRes {

    //10개 정도를 넘겨 준다고 생각 하면됨

    //타임라인 id
    private Long timelineId;

    //썸네일
    private String imageUrl;

    //여행 제목
    private String title;

    //여행자 닉네임
    private String nickname;

    // =>여행기간 localdatetime으로 하면 안됨
    //여행 시작
    //ex)2019-11-13
    private String createTime;//생성시간

    //여행 마감
    private String finishTime;//완료시간

    private String startPlace;

    private String finishPlace;

    public static String make(LocalDateTime date) {

        LocalDate s2 = date.toLocalDate();
        String s3 = s2.toString();
        s3 = s3.replace("-", ".");
        return s3;
    }

    public static MainTimelinePhotoDtoResBuilder builder(TimeLine timeline, Post startpost, Post lastpost, Photo photo, User user) {

        String start = "";
        String finish = "";
        if (timeline.getCreateTime() != null) {
            start = make(timeline.getCreateTime());
        }

        if (timeline.getFinishTime() != null) {
            finish = make(timeline.getFinishTime());
        }

        return MainTimelinePhotoDtoResBuilder().
                timelineId(timeline.getTimelineId())
                .imageUrl(photo.getPhotoUrl())
                .title(timeline.getTitle())
                .nickname(user.getNickname())
                .createTime(start)
                .finishTime(finish)
                .startPlace(startpost.getAddress2())
                .finishPlace(lastpost.getAddress2());
    }

}
