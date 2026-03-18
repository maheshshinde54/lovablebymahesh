package com.starter.lovable.service.impl;

import com.starter.lovable.dto.member.InviteMemberRequest;
import com.starter.lovable.dto.member.MemberResponse;
import com.starter.lovable.dto.member.UpdateMemberRoleRequest;
import com.starter.lovable.service.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProjectMemberServiceImp implements ProjectMemberService {
    @Override
    public List<MemberResponse> getProjectMembers(Long projectId,
                                                  Long userId)
    {
        return List.of();
    }

    @Override
    public MemberResponse inviteMember(Long projectId,
                                       InviteMemberRequest request,
                                       Long userId)
    {
        return null;
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId,
                                           Long memberId,
                                           UpdateMemberRoleRequest request,
                                           Long userId)
    {
        return null;
    }

    @Override
    public MemberResponse deleteProjectMember(Long projectId,
                                              Long memberId,
                                              Long userId)
    {
        return null;
    }
}
