package com.moyeo.main.dto;

import com.moyeo.main.entity.Post;
import lombok.Builder;
import lombok.Getter;

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


    private List<String> photoList; // photoId 리스트

    private Boolean isFavorite;//정보를 가지고 온 유저가 , 해당 포스트에 좋아요를 눌렀는가 여부에 대해

    private Long favoriteCount;



    private Boolean isTraveling;


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

}
