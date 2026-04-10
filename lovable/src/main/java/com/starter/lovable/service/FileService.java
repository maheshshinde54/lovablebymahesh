package com.starter.lovable.service;

import com.starter.lovable.dto.project.FileContentResponse;
import com.starter.lovable.dto.project.FileNode;

import java.util.List;

public interface FileService {
    List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFile(Long projectId, String path, Long userId);
}


