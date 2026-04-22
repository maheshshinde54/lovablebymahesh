package com.starter.lovable.service.impl;

import com.starter.lovable.dto.project.FileContentResponse;
import com.starter.lovable.dto.project.FileNode;
import com.starter.lovable.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId)
    {
        log.info("FileServiceImpl.getFileTree called projectId={} userId={}", projectId, userId);
        log.debug("FileServiceImpl.getFileTree returning empty list placeholder");
        return List.of();
    }

    @Override
    public FileContentResponse getFile(Long projectId, String path, Long userId)
    {
        log.info("FileServiceImpl.getFile called projectId={} path={} userId={}", projectId, path, userId);
        log.debug("FileServiceImpl.getFile returning null placeholder");
        return null;
    }
}
