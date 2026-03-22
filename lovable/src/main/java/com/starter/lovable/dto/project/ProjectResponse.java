package com.starter.lovable.dto.project;

import com.starter.lovable.dto.auth.UserProfileResponse;

import java.time.Instant;

public record ProjectResponse
        (Long id,
         String name,
         Instant createdAt,
         Instant updatedAt,
         UserProfileResponse owner)
{
}
