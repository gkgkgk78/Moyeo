package com.moyeo.main.dto;

import java.time.LocalDateTime;

import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.TimeLine;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@Builder(builderMethodName = "GetPostResBuilder")
public class GetPostRes {
    private Long timelineId;
    private String thumbNail;
    private Long postId;
    private Long totalFavorite;
    private String timelineTitle;
    private Boolean isMoyeo;
    private LocalDateTime createTime;

    public static GetPostRes.GetPostResBuilder builder(Post post, Long totalFavorite) {

        Long timelineId = post.getTimelineId().getTimelineId();
        String thumbNail = post.getPhotoList().get(0).getPhotoUrl();
        Long postId = post.getPostId();
        Long total = totalFavorite;
        String timelineTitle = post.getTimelineId().getTitle();

        return GetPostResBuilder()
                .timelineId(timelineId)
                .thumbNail(thumbNail)
                .postId(postId)
                .totalFavorite(total)
                .timelineTitle(timelineTitle)
                .createTime(post.getCreateTime())
                .isMoyeo(false);
    }

    public static GetPostRes.GetPostResBuilder builder(MoyeoPost post) {

        return GetPostResBuilder()
            .timelineId(null)
            .thumbNail(post.getMoyeoPhotoList().get(0).getPhotoUrl())
            .postId(post.getMoyeoPostId())
            .totalFavorite(post.getFavoriteCount())
            .timelineTitle(null)
            .createTime(post.getCreateTime())
            .isMoyeo(true);
    }
}
