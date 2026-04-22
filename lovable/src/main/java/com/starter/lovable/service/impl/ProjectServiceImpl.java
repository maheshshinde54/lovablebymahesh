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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
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
        log.info("ProjectServiceImpl.getUserProjects called for userId={}", userId);
        List<Project> getAllProjectDetails = projectRepository.findAllAccessibleProjectsByUser(userId);
        log.debug("Found {} accessible projects for userId={}", getAllProjectDetails.size(), userId);
        return projectMapper.toListOfProjects(getAllProjectDetails);
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectById(Long projectId)
    {
        Long userId = authUtil.getCurrentUserId();
        log.info("ProjectServiceImpl.getUserProjectById called for projectId={} userId={}", projectId, userId);
        Project project = getAccessibleProjectById(projectId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request)
    {
        Long userId = authUtil.getCurrentUserId();
        log.info("ProjectServiceImpl.createProject called for userId={}", userId);
        log.debug("ProjectRequest payload: {}", request);

//        User owner = userRepository.findById(userId)
//                                   .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        User owner = userRepository.getReferenceById(userId);

        if(!subscriptionService.canCreateNewProject())
        {
            log.warn("Subscription plan prevents project creation for userId={}", userId);
            throw new BadRequestException("User can not create a new project with current plan. Please upgrade plan");
        }

        Project project = Project.builder()
                                 .name(request.name())
                                 .build();
        project = projectRepository.save(project);
        log.info("ProjectServiceImpl.createProject persisted new projectId={}", project.getId());

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
        log.debug("Project owner membership created for projectId={} ownerId={}", project.getId(), owner.getId());

        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long id, ProjectRequest request)
    {
        log.info("ProjectServiceImpl.updateProject called for projectId={}", id);
        log.debug("Project update payload: {}", request);
        Project project = getAccessibleProjectById(id);
        project.setName(request.name());
        ProjectResponse response = projectMapper.toProjectResponse(project);
        log.info("ProjectServiceImpl.updateProject completed for projectId={}", id);
        return response;
    }

    @Override
    public Void softDelete(Long id)
    {
        log.info("ProjectServiceImpl.softDelete called for projectId={}", id);
        Project project = getAccessibleProjectById(id);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
        log.info("ProjectServiceImpl.softDelete marked deleted for projectId={}", id);

        return null;
    }

    //Internal Function
    public Project getAccessibleProjectById(Long projectId)
    {
        Long userId = authUtil.getCurrentUserId();
        log.debug("ProjectServiceImpl.getAccessibleProjectById called for projectId={} userId={}", projectId, userId);
        return projectRepository.findAllAccessibleProjectsById(projectId, userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
