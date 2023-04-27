package com.moyeo.entity;

import com.moyeo.dto.MainTimelinePhotoDtoRes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@RedisHash(value = "page", timeToLive = 300)
public class RedisPage {
    @Id
    private Integer num;

    @Indexed
    private List<MainTimelinePhotoDtoRes> list;

    public RedisPage(Integer num, List<MainTimelinePhotoDtoRes> list){
        this.num = num;
        this.list = list;
    }
}
