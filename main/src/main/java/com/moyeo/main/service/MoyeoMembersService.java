package com.moyeo.main.service;

import com.moyeo.main.dto.RegistMoyeoRes;
import com.moyeo.main.entity.User;

public interface MoyeoMembersService {
    public RegistMoyeoRes registMoyeoMembers(User user, Long moyeoTimelineId) throws Exception;
    public RegistMoyeoRes updateMoyeoMembers(User user, Long moyeoTimelineId) throws Exception;

}
