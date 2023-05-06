package com.moyeo.main.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.moyeo.main.dto.MainTimelinePhotoDtoRes;
import com.moyeo.main.dto.TimelinePostOuter;
import com.moyeo.main.entity.TimeLine;
import com.moyeo.main.entity.User;

public interface TimeLineService2 {
    TimelinePostOuter searchOneTimeline(Long uid,User user) throws Exception;

}
