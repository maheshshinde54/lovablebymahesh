package com.starter.lovable.controller;

import com.starter.lovable.dto.project.FileContentResponse;
import com.starter.lovable.dto.project.FileNode;
import com.starter.lovable.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/projects/{projectId}/files")
public class FileController {
    private final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileNode>> getFileTree(@PathVariable Long projectId)
    {
        log.info("FileController.getFileTree called for projectId={}", projectId);
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFileTree(projectId, userId));
    }

    @GetMapping("/{*path}")
    public ResponseEntity<FileContentResponse> getFile(@PathVariable Long projectId, @PathVariable String path)
    {
        log.info("FileController.getFile called for projectId={} path={}", projectId, path);
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFile(projectId, path, userId));
    }
}
