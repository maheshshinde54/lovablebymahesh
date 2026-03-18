package com.starter.lovable.service.impl;

import com.starter.lovable.dto.project.ProjectRequest;
import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;
import com.starter.lovable.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProjectServiceImpl implements ProjectService
{
    @Override
    public List<ProjectSummeryResponse> getUserProjects(Long userId)
    {
        return List.of();
    }

    @Override
    public ProjectResponse getProjectById(Long id)
    {
        return null;
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request,
                                         Long userId)
    {
        return null;
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
