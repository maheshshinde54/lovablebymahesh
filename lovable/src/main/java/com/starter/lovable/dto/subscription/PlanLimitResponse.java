package com.starter.lovable.dto.subscription;

public record PlanLimitResponse(String planName,
                                Integer maxTokenPerDay,
                                Integer maxProjects,
                                Boolean unlimitedAi) {
}
