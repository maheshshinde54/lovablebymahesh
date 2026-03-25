package com.starter.lovable.service.impl;

import com.starter.lovable.dto.subscription.PlanLimitResponse;
import com.starter.lovable.dto.subscription.UsageTodayResponse;
import com.starter.lovable.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId)
    {
        return null;
    }

    @Override
    public PlanLimitResponse getCurrentSubscriptionLimitOfUser(Long userId)
    {
        return null;
    }
}
