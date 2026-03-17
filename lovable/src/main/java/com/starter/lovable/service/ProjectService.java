package com.starter.lovable.service;

import com.starter.lovable.dto.project.ProjectRequest;
import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;

import java.util.List;

public interface ProjectService
{
    List<ProjectSummeryResponse> getUserProjects(Long userId);

    ProjectResponse getProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request,
                                  Long userId);

    ProjectResponse updateProject(Long id,
                                  ProjectRequest request,
                                  Long userId);

    Void softDelete(Long id,
                    Long userId);
}



