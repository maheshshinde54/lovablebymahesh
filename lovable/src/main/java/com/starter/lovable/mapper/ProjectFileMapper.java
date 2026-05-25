package com.starter.lovable.mapper;

import com.starter.lovable.dto.project.FileNode;
import com.starter.lovable.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper
{
    List<FileNode> toListOfFileNode(List<ProjectFile> projectFiles);
}
