package com.starter.lovable.service.impl;

import com.starter.lovable.dto.member.InviteMemberRequest;
import com.starter.lovable.dto.member.MemberResponse;
import com.starter.lovable.dto.member.UpdateMemberRoleRequest;
import com.starter.lovable.entity.Project;
import com.starter.lovable.entity.ProjectMember;
import com.starter.lovable.entity.ProjectMemberId;
import com.starter.lovable.entity.User;
import com.starter.lovable.mapper.ProjectMemberMapper;
import com.starter.lovable.respository.ProjectMemberRepository;
import com.starter.lovable.respository.ProjectRepository;
import com.starter.lovable.respository.UserRepository;
import com.starter.lovable.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImp implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;


    @Override
    public List<MemberResponse> getProjectMembers(Long projectId,
                                                  Long userId)
    {
        Project project = getAccessibleProjectById(projectId, userId);

        List<MemberResponse> memberResponseList = new ArrayList<>();

        memberResponseList.add(projectMemberMapper.toProjectMemberResponseFromOwner(project.getOwner()));

        memberResponseList.addAll(projectMemberRepository.findByIdProjectId(projectId)
                                                         .stream()
                                                         .map(projectMemberMapper::toProjectMemberResponseFromMember)
                                                         .toList());
        return memberResponseList;
    }

    @Override
    public MemberResponse inviteMember(Long projectId,
                                       InviteMemberRequest request,
                                       Long userId)
    {
        Project project = getAccessibleProjectById(projectId, userId);
        if (!project.getOwner()
                    .getId()
                    .equals(userId))
        {
            throw new RuntimeException("Not Allowed");
        }
        User invitee = userRepository.findByEmail(request.email())
                                     .orElseThrow();
        if (invitee.getId()
                   .equals(userId))
        {
            throw new RuntimeException("Can not invite your self");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if (projectMemberRepository.existsById(projectMemberId))
        {
            throw new RuntimeException("Can not invite once again");

        }
        ProjectMember member = ProjectMember.builder()
                                            .id(projectMemberId)
                                            .project(project)
                                            .user(invitee)
                                            .projectRole(request.role())
                                            .invitedAt(Instant.now())
                                            .build();
        projectMemberRepository.save(member);


        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId,
                                           Long memberId,
                                           UpdateMemberRoleRequest request,
                                           Long userId)
    {
        Project project = getAccessibleProjectById(projectId, userId);
        if (!project.getOwner()
                    .getId()
                    .equals(userId))
        {
            throw new RuntimeException("Not Allowed");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                                                             .orElseThrow();
        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    public void removeProjectMember(Long projectId,
                                    Long memberId,
                                    Long userId)
    {
        // 1. Get project and verify the requester is the OWNER
        Project project = getAccessibleProjectById(projectId, userId);

        if (!project.getOwner()
                    .getId()
                    .equals(userId))
        {
            throw new RuntimeException("Only the project owner can remove members.");
        }

        // 2. Prevent owner from removing themselves (Optional but recommended)
        if (memberId.equals(userId))
        {
            throw new RuntimeException("You cannot remove yourself from your own project.");
        }

        // 3. Create the ID and check if the member actually exists
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);

        if (!projectMemberRepository.existsById(projectMemberId))
        {
            throw new RuntimeException("Member not found in this project.");
        }

        // 4. Perform the deletion
        projectMemberRepository.deleteById(projectMemberId);
    }

    //Internal Function
    public Project getAccessibleProjectById(Long projectId,
                                            Long userId)
    {
        return projectRepository.findAllAccessibleProjectsById(projectId, userId)
                                .orElseThrow();
    }
}
