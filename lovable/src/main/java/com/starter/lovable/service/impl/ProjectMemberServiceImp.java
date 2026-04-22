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
import com.starter.lovable.security.AuthUtil;
import com.starter.lovable.service.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class ProjectMemberServiceImp implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;
    AuthUtil authUtil;


    @Override
    @PreAuthorize("@security.canViewMembers(#projectId)")
    public List<MemberResponse> getProjectMembers(Long projectId, Long userId)
    {
        log.info("ProjectMemberServiceImp.getProjectMembers called projectId={} userId={}", projectId, userId);
        Project project = getAccessibleProjectById(projectId, userId);
        log.debug("ProjectMemberServiceImp.getProjectMembers verified access for projectId={} userId={} projectName={}", projectId, userId, project.getName());
        return projectMemberRepository.findByIdProjectId(projectId)
                                      .stream()
                                      .map(projectMemberMapper::toProjectMemberResponseFromMember)
                                      .toList();
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId)
    {
        Project project = getAccessibleProjectById(projectId, userId);
        log.info("ProjectMemberServiceImp.inviteMember called projectId={} userId={} inviteUser={}", projectId, userId, request.userName());
        User invitee = userRepository.findByUserName(request.userName())
                                     .orElseThrow();
        if (invitee.getId()
                   .equals(userId))
        {
            log.warn("ProjectMemberServiceImp.inviteMember rejected self-invitation userId={}", userId);
            throw new RuntimeException("Can not invite your self");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, invitee.getId());
        if (projectMemberRepository.existsById(projectMemberId))
        {
            log.warn("ProjectMemberServiceImp.inviteMember rejected duplicate invitation projectId={} inviteeId={}", projectId, invitee.getId());
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
        log.info("ProjectMemberServiceImp.inviteMember successfully invited memberId={} to projectId={}", invitee.getId(), projectId);

        return projectMemberMapper.toProjectMemberResponseFromMember(member);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request, Long userId)
    {
        log.info("ProjectMemberServiceImp.updateMemberRole called projectId={} memberId={} newRole={}", projectId, memberId, request.role());
        Project project = getAccessibleProjectById(projectId, userId);

        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);
        ProjectMember projectMember = projectMemberRepository.findById(projectMemberId)
                                                             .orElseThrow();
        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        log.info("ProjectMemberServiceImp.updateMemberRole updated role for memberId={} in projectId={}", memberId, projectId);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    @PreAuthorize("@security.canManageMembers(#projectId)")
    public void removeProjectMember(Long projectId, Long memberId, Long userId)
    {
        log.info("ProjectMemberServiceImp.removeProjectMember called projectId={} memberId={}", projectId, memberId);
        // 1. Get project and verify the requester is the OWNER
        Project project = getAccessibleProjectById(projectId, userId);

        // 2. Prevent owner from removing themselves (Optional but recommended)
        if (memberId.equals(userId))
        {
            log.warn("ProjectMemberServiceImp.removeProjectMember rejected self-removal userId={}", userId);
            throw new RuntimeException("You cannot remove yourself from your own project.");
        }

        // 3. Create the ID and check if the member actually exists
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, memberId);

        if (!projectMemberRepository.existsById(projectMemberId))
        {
            log.warn("ProjectMemberServiceImp.removeProjectMember member not found projectId={} memberId={}", projectId, memberId);
            throw new RuntimeException("Member not found in this project.");
        }

        // 4. Perform the deletion
        projectMemberRepository.deleteById(projectMemberId);
        log.info("ProjectMemberServiceImp.removeProjectMember successfully removed memberId={} from projectId={}", memberId, projectId);
    }

    //Internal Function
    public Project getAccessibleProjectById(Long projectId, Long userId)
    {
        log.debug("ProjectMemberServiceImp.getAccessibleProjectById called projectId={} userId={}", projectId, userId);
        return projectRepository.findAllAccessibleProjectsById(projectId, userId)
                                .orElseThrow();
    }
}
