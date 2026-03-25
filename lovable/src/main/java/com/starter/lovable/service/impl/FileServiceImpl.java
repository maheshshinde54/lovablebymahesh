package com.starter.lovable.service.impl;

import com.starter.lovable.dto.project.FileContentResponse;
import com.starter.lovable.dto.project.FileNode;
import com.starter.lovable.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<FileNode> getFileTree(Long projectId,
                                      Long userId)
    {
        return List.of();
    }

    @Override
    public FileContentResponse getFile(Long projectId,
                                       String path,
                                       Long userId)
    {
        return null;
    }
}
