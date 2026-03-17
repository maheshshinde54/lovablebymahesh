package com.starter.lovable.dto.member;

import com.starter.lovable.enums.ProjectRole;

import java.lang.reflect.Member;

public record UpdateMemberRoleRequest(
        ProjectRole role
) {
}
