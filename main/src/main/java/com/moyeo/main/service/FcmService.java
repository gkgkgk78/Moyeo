package com.moyeo.main.service;

import com.moyeo.main.entity.User;
import com.moyeo.main.exception.BaseException;

public interface FcmService {

    public void send(User user) throws BaseException;

    public void send(User user, Long moyeoTimelineId, String title, String content) throws BaseException;
}
