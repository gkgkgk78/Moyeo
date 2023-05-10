package com.moyeo.main.dto;

import com.moyeo.main.entity.Post;
import com.moyeo.main.entity.User;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(builderMethodName = "PostInsertReqBuilder")
public class PostInsertReq {

    private String deviceToken;
    private String address1;//위치 정보
    private String address2;
    private String address3;
    private String address4;
    private Long userId;//고유한 사용자 아이디


    public static PostInsertReqBuilder builder(AddPostReq post, User user) {

        return PostInsertReqBuilder()
                .address1(post.getAddress1())
                .address2(post.getAddress2())
                .address3(post.getAddress3())
                .address4(post.getAddress4())
                .userId(user.getUserId())
                .deviceToken(user.getDeviceToken())
                ;
    }

}
