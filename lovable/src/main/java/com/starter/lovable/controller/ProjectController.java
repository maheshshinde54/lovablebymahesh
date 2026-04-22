package com.starter.lovable.controller;

import com.starter.lovable.dto.project.ProjectRequest;
import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;
import com.starter.lovable.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping()
    public ResponseEntity<List<ProjectSummeryResponse>> getProjects()
    {
        log.info("ProjectController.getProjects called");
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getUserProjectById(@PathVariable Long id)
    {
        log.info("ProjectController.getUserProjectById called for projectId={}", id);
        return ResponseEntity.ok(projectService.getUserProjectById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request)
    {
        log.info("ProjectController.createProject called");
        log.debug("createProject request body: {}", request);
        ProjectResponse projectResponse = projectService.createProject(request);
        log.info("ProjectController.createProject created projectId={}", projectResponse.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(projectResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                         @RequestBody @Valid ProjectRequest request)
    {
        log.info("ProjectController.updateProject called for projectId={}", id);
        log.debug("updateProject request body: {}", request);
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id)
    {
        log.info("ProjectController.deleteProject called for projectId={}", id);
        return ResponseEntity.ok(projectService.softDelete(id));
    }

}
