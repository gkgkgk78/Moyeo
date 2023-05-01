package com.moyeo.main.service;

public interface YeobotService {

    public String[] findLatestAddress(long userId) throws Exception;
    public String getLatestTimelineStatus(long userId);

}
