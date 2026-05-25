package com.starter.lovable.service.impl;

import com.starter.lovable.dto.project.FileContentResponse;
import com.starter.lovable.dto.project.FileNode;
import com.starter.lovable.entity.Project;
import com.starter.lovable.entity.ProjectFile;
import com.starter.lovable.error.ResourceNotFoundException;
import com.starter.lovable.mapper.ProjectFileMapper;
import com.starter.lovable.respository.ProjectFileRepository;
import com.starter.lovable.respository.ProjectRepository;
import com.starter.lovable.service.ProjectFileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService
{
    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;
    @Value("${minio.project-bucket}")
    private String projectBucket;

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId)
    {
        log.info("ProjectFileServiceImpl.getFileTree called projectId={} userId={}", projectId, userId);

        List<ProjectFile> projectFileList = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toListOfFileNode(projectFileList);
    }

    @Override
    public FileContentResponse getFile(Long projectId, String path, Long userId)
    {
        log.info("ProjectFileServiceImpl.getFile called projectId={} path={} userId={}", projectId, path, userId);
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String content)
    {
        log.info("Saving files: {}", filePath);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        String cleanPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        String objectKey = projectId + "/" + cleanPath;

        try {
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            InputStream inputStream = new ByteArrayInputStream(contentBytes);

            //saving file content
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(projectBucket)
                    .object(objectKey)
                    .stream(inputStream, contentBytes.length, -1)
                    .contentType(determineContentType(cleanPath))
                    .build());

            //saving metaData
            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build());
            file.setUpdatedAt(Instant.now());
            projectFileRepository.save(file);

            log.info("Saved file : {}", file);

        } catch (Exception e) {

            log.error("unable to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("Failed to save file", e);
        }

    }

    private String determineContentType(String path)
    {
        String type = URLConnection.guessContentTypeFromName(path);
        if (type != null) return type;
        if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".css")) return "text/css";

        return "text/plain";
    }

}
