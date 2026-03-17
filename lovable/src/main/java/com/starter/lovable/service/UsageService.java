package com.starter.lovable.service;

import com.starter.lovable.dto.subscription.PlanLimitResponse;
import com.starter.lovable.dto.subscription.UsageTodayResponse;

public interface UsageService {
    UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitResponse getCurrentSubscriptionLimitOfUser(Long userId);
}

