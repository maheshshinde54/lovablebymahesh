package com.starter.lovable.controller;

import com.starter.lovable.dto.project.ProjectRequest;
import com.starter.lovable.dto.project.ProjectResponse;
import com.starter.lovable.dto.project.ProjectSummeryResponse;
import com.starter.lovable.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/projects")
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping()
    public ResponseEntity<List<ProjectSummeryResponse>> getProjects()
    {
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getUserProjectById(@PathVariable Long id)
    {

        return ResponseEntity.ok(projectService.getUserProjectById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest request)
    {

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(projectService.createProject(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                         @RequestBody @Valid ProjectRequest request)
    {

        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id)
    {

        return ResponseEntity.ok(projectService.softDelete(id));
    }

}
