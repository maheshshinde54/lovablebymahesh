package com.starter.lovable.controller;

import com.starter.lovable.dto.member.InviteMemberRequest;
import com.starter.lovable.dto.member.MemberResponse;
import com.starter.lovable.dto.member.UpdateMemberRoleRequest;
import com.starter.lovable.service.ProjectMemberService;
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
@RequestMapping("api/projects/{projectId}/members")
public class ProjectMemberController {
    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId)
    {
        log.info("ProjectMemberController.getProjectMembers called for projectId={}", projectId);
        Long userId = 1L;
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId, userId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(@PathVariable Long projectId,
                                                       @RequestBody @Valid InviteMemberRequest request)
    {
        log.info("ProjectMemberController.inviteMember called for projectId={}", projectId);
        log.debug("InviteMemberRequest payload: {}", request);
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(projectMemberService.inviteMember(projectId, request, userId));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId, @PathVariable Long memberId,
                                                           @RequestBody @Valid UpdateMemberRoleRequest request)
    {
        log.info("ProjectMemberController.updateMemberRole called for projectId={} memberId={}", projectId, memberId);
        log.debug("UpdateMemberRoleRequest payload: {}", request);
        Long userId = 1L;
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, request, userId));

    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeProjectMember(@PathVariable Long projectId, @PathVariable Long memberId)
    {
        log.info("ProjectMemberController.removeProjectMember called for projectId={} memberId={}", projectId, memberId);
        Long userId = 1L;
        projectMemberService.removeProjectMember(projectId, memberId, userId);
        return ResponseEntity.noContent()
                             .build();

    }

}
