package com.starter.lovable.dto.subscription;

public record PlanResponse(Long id,
                           String name,
                           String stripePriceId,
                           Integer maxTokensPerDay,
                           Integer maxPreviews,
                           Boolean unlimitedAi,
                           Boolean isActive
) {
}
