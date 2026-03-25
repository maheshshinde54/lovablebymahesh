package com.starter.lovable.dto.member;

import com.starter.lovable.enums.ProjectRole;

public record InviteMemberRequest(String email,
                                  ProjectRole role) {
}
