package com.moyeo.main.dto;

import com.moyeo.main.entity.MoyeoPhoto;
import com.moyeo.main.entity.MoyeoPost;
import com.moyeo.main.entity.Photo;
import com.moyeo.main.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder(builderMethodName = "MyPostDtoResBuilder")
@Getter
public class MyPostDtoRes {
    private Long postId;
    private String voiceUrl;
    private Double voiceLength;
    private String address2;
    private String address3;
    private String address4;
    private String text;
    private List<String> photoList; // photoUrl 리스트
    private Boolean isFavorite; //정보를 가지고 온 유저가 , 해당 포스트에 좋아요를 눌렀는가 여부에 대해
    private Long favoriteCount;
    private Boolean isMoyeo;
    private List<PostMembers> members;

    public static MyPostDtoResBuilder builder(Post post,List<String>photoList,Long favoriteCount,Boolean favorite) {

        return MyPostDtoResBuilder()
                .postId(post.getPostId())
                .voiceUrl(post.getVoiceUrl())
                .voiceLength(post.getVoiceLength())
                .address2(post.getAddress2())
                .address3(post.getAddress3())
                .address4(post.getAddress4())
                .text(post.getText())
                .photoList(photoList)
                .favoriteCount(favoriteCount)
                .isFavorite(favorite)
                ;
    }

    public static MyPostDtoResBuilder builder(Post entity, Boolean isFavorite) {
        List<String> photos = new ArrayList<>();
        for (Photo photo : entity.getPhotoList()) {
            photos.add(photo.getPhotoUrl());
        }

        return MyPostDtoResBuilder()
            .postId(entity.getPostId())
            .voiceUrl(entity.getVoiceUrl())
            .voiceLength(entity.getVoiceLength())
            .address2(entity.getAddress2())
            .address3(entity.getAddress3())
            .address4(entity.getAddress4())
            .text(entity.getText())
            .photoList(photos)
            .favoriteCount(entity.getFavoriteCount())
            .isFavorite(isFavorite)
            .isMoyeo(false)
            .members(null)
            ;

    }

    public static MyPostDtoResBuilder builder(BasePostDto basePost) {
        return MyPostDtoResBuilder()
            .postId(basePost.getPostId())
            .voiceUrl(basePost.getVoiceUrl())
            .voiceLength(basePost.getVoiceLength())
            .address2(basePost.getAddress2())
            .address3(basePost.getAddress3())
            .address4(basePost.getAddress4())
            .text(basePost.getText())
            .photoList(basePost.getPhotoList())
            .favoriteCount(basePost.getFavoriteCount())
            .isFavorite(basePost.getIsFavorite())
            .isMoyeo(basePost.getIsMoyeo())
            .members(basePost.getMembers())
            ;

    }
}
