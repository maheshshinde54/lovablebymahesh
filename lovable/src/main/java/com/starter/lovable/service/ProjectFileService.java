package com.starter.lovable.service;

import com.starter.lovable.dto.project.FileContentResponse;
import com.starter.lovable.dto.project.FileNode;

import java.util.List;

public interface ProjectFileService
{
    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFile(Long projectId, String path, Long userId);

    void saveFile(Long projectId, String filePath, String fileContent);
}



