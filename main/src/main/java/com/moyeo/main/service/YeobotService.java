package com.moyeo.main.service;

import java.util.List;

public interface YeobotService {

    public List<String[]> findLatestAddress(Long userId) throws Exception;
    public String getLatestTimelineStatus(Long userId);

}
