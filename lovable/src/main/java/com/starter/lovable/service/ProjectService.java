package com.starter.lovable.service;

import com.starter.lovable.dto.project.ProjectRequest;
import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;

import java.util.List;

public interface ProjectService {
    List<ProjectSummeryResponse> getUserProjects();

    ProjectResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(Long id,
                                  ProjectRequest request);

    Void softDelete(Long id);
}



