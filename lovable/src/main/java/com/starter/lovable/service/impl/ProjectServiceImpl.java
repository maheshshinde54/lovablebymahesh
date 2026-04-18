package com.starter.lovable.service.impl;

import com.starter.lovable.dto.project.ProjectRequest;
import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;
import com.starter.lovable.entity.Project;
import com.starter.lovable.entity.ProjectMember;
import com.starter.lovable.entity.ProjectMemberId;
import com.starter.lovable.entity.User;
import com.starter.lovable.enums.ProjectRole;
import com.starter.lovable.error.BadRequestException;
import com.starter.lovable.error.ResourceNotFoundException;
import com.starter.lovable.mapper.ProjectMapper;
import com.starter.lovable.respository.ProjectMemberRepository;
import com.starter.lovable.respository.ProjectRepository;
import com.starter.lovable.respository.UserRepository;
import com.starter.lovable.security.AuthUtil;
import com.starter.lovable.service.ProjectService;
import com.starter.lovable.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectServiceImpl implements ProjectService {
    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;
    SubscriptionService subscriptionService;

    @Override
    public List<ProjectSummeryResponse> getUserProjects()
    {

        Long userId = authUtil.getCurrentUserId();
        List<Project> getAllProjectDetails = projectRepository.findAllAccessibleProjectsByUser(userId);
        return projectMapper.toListOfProjects(getAllProjectDetails);

    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectById(Long projectId)
    {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request)
    {
        Long userId = authUtil.getCurrentUserId();
//        User owner = userRepository.findById(userId)
//                                   .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        User owner = userRepository.getReferenceById(userId);
        if(!subscriptionService.canCreateNewProject())
        {
            throw new BadRequestException("User can not create a new project with current plan. Please upgrade plan");
        }
        Project project = Project.builder()
                                 .name(request.name())
                                 .build();
        project = projectRepository.save(project);
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getId(), owner.getId());

        ProjectMember projectMember = ProjectMember.builder()
                                                   .id(projectMemberId)
                                                   .project(project)
                                                   .user(owner)
                                                   .projectRole(ProjectRole.OWNER)
                                                   .invitedAt(Instant.now())
                                                   .acceptedAt(Instant.now())
                                                   .build();
        projectMemberRepository.save(projectMember);

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")

    public ProjectResponse updateProject(Long id, ProjectRequest request)
    {
        Project project = getAccessibleProjectById(id);

        project.setName(request.name());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public Void softDelete(Long id)
    {
        Project project = getAccessibleProjectById(id);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);

        return null;
    }

    //Internal Function
    public Project getAccessibleProjectById(Long projectId)
    {
        Long userId = authUtil.getCurrentUserId();
        return projectRepository.findAllAccessibleProjectsById(projectId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
