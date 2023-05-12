package com.moyeo.main.service;

import java.util.List;

import com.moyeo.main.dto.InviteMoyeoMembersRes;
import com.moyeo.main.dto.MoyeoMembersReq;
import com.moyeo.main.dto.RegistMoyeoRes;
import com.moyeo.main.entity.User;

public interface MoyeoMembersService {
    public InviteMoyeoMembersRes inviteMoyeoMembers2(User inviter, Long moyeoTimelineId, List<MoyeoMembersReq> userIdList) throws Exception;
    public Boolean inviteMoyeoMembers(User user, List<MoyeoMembersReq> moyeoMembersReqList) throws Exception;
    public RegistMoyeoRes registMoyeoMembers(User user, Long moyeoTimelineId) throws Exception;
    public Boolean updateMoyeoMembers(User user, Long moyeoTimelineId) throws Exception;

}
