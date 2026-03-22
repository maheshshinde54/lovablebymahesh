package com.starter.lovable.mapper;

import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;
import com.starter.lovable.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper
{
    ProjectResponse toProjectResponse (Project project);

    List<ProjectSummeryResponse> toListOfProjects(List<Project> projects);

    ProjectSummeryResponse toProjectSummeryResponse (Project project);
}
