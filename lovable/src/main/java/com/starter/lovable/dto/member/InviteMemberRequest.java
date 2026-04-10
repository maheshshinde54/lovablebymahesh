package com.starter.lovable.dto.member;

import com.starter.lovable.enums.ProjectRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InviteMemberRequest(

        @NotBlank String userName,

        @NotNull ProjectRole role) {
}
