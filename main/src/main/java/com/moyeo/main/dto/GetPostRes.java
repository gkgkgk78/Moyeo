package com.moyeo.main.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@Builder(builderMethodName = "GetPostResBuilder")
public class GetPostRes {
    private List<GetPostResTimelineInfo> timelineInfoList;
    private String thumbNail;
    private Long postId;
    private Long totalFavorite;
    private String timelineTitle;
    private Boolean isMoyeo;
    private LocalDateTime createTime;

    public static GetPostRes.GetPostResBuilder builder(Post post, Long totalFavorite) {

        TimeLine postTimeline = post.getTimelineId();
        User timelineUser = postTimeline.getUserId();
        List<GetPostResTimelineInfo> timelineInfoList = new ArrayList<>();
        timelineInfoList.add(GetPostResTimelineInfo.builder()
            .timelineId(postTimeline.getTimelineId())
            .userNickname(timelineUser.getNickname())
            .userProfileImageUrl(timelineUser.getProfileImageUrl()).build());

        String thumbNail = post.getPhotoList().get(0).getPhotoUrl();
        Long postId = post.getPostId();
        Long total = totalFavorite;
        String timelineTitle = post.getTimelineId().getTitle();

        return GetPostResBuilder()
                .timelineInfoList(timelineInfoList)
                .thumbNail(thumbNail)
                .postId(postId)
                .totalFavorite(total)
                .timelineTitle(timelineTitle)
                .createTime(post.getCreateTime())
                .isMoyeo(false);
    }

    public static GetPostRes.GetPostResBuilder builder(MoyeoPost post, List<TimeLine> timeLineList) {

        List<GetPostResTimelineInfo> timelineInfoList = new ArrayList<>();
        for(TimeLine timeLine : timeLineList) {
            timelineInfoList.add(GetPostResTimelineInfo.builder()
                .timelineId(timeLine.getTimelineId())
                .userNickname(timeLine.getUserId().getNickname())
                .userProfileImageUrl(timeLine.getUserId().getProfileImageUrl()).build());
        }

        return GetPostResBuilder()
            .timelineInfoList(timelineInfoList)
            .thumbNail(post.getMoyeoPhotoList().get(0).getPhotoUrl())
            .postId(post.getMoyeoPostId())
            .totalFavorite(post.getFavoriteCount())
            .timelineTitle(null)
            .createTime(post.getCreateTime())
            .isMoyeo(true);
    }
}
