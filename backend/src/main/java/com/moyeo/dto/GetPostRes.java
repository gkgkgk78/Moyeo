package com.moyeo.dto;

import com.moyeo.entity.Post;

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
                .timelineTitle(timelineTitle);
    }
}
