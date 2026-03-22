package com.starter.lovable.service.impl;

import com.starter.lovable.dto.project.ProjectRequest;
import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;
import com.starter.lovable.entity.Project;
import com.starter.lovable.entity.User;
import com.starter.lovable.mapper.ProjectMapper;
import com.starter.lovable.respository.ProjectRepository;
import com.starter.lovable.respository.UserRepository;
import com.starter.lovable.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectServiceImpl implements ProjectService {
    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Override
    public List<ProjectSummeryResponse> getUserProjects(Long userId)
    {
        List<Project> getAllProjectDetails = projectRepository.findAllAccessibleProjectsByUser(userId);
        return projectMapper.toListOfProjects(getAllProjectDetails);

//        return projectRepository.findAllAccessibleProjectsByUser(userId)
//                                .stream()
//                                .map(projectMapper::toProjectSummeryResponse)
//                                .toList();
    }

    @Override
    public ProjectResponse getProjectById(Long id)
    {
        Project getProjectDetails = projectRepository.findById(id)
                                                     .orElseThrow();

        return projectMapper.toProjectResponse(getProjectDetails);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request,
                                         Long userId)
    {
        User owner = userRepository.findById(userId)
                                   .orElseThrow();
        Project project = Project.builder()
                                 .name(request.name())
                                 .owner(owner)
                                 .build();
        project = projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id,
                                         ProjectRequest request,
                                         Long userId)
    {
        return null;
    }

    @Override
    public Void softDelete(Long id,
                           Long userId)
    {
        return null;
    }
}
