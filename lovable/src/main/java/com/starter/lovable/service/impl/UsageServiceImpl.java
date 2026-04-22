package com.starter.lovable.service.impl;

import com.starter.lovable.dto.subscription.PlanLimitResponse;
import com.starter.lovable.dto.subscription.UsageTodayResponse;
import com.starter.lovable.service.UsageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId)
    {
        log.info("UsageServiceImpl.getTodayUsageOfUser called for userId={}", userId);
        log.debug("UsageServiceImpl.getTodayUsageOfUser returning null placeholder");
        return null;
    }

    @Override
    public PlanLimitResponse getCurrentSubscriptionLimitOfUser(Long userId)
    {
        log.info("UsageServiceImpl.getCurrentSubscriptionLimitOfUser called for userId={}", userId);
        log.debug("UsageServiceImpl.getCurrentSubscriptionLimitOfUser returning null placeholder");
        return null;
    }
}
