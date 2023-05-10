package com.moyeo.main.service;

import com.moyeo.main.dto.MoyeoMembersReq;
import com.moyeo.main.dto.RegistMoyeoRes;
import com.moyeo.main.entity.User;

public interface MoyeoMembersService {
    public Long inviteMoyeoMembers(User user, MoyeoMembersReq moyeoMembersReq) throws Exception;
    public RegistMoyeoRes registMoyeoMembers(User user, Long moyeoTimelineId) throws Exception;
    public Boolean updateMoyeoMembers(User user, Long moyeoTimelineId) throws Exception;

}
