package com.starter.lovable.service.impl;

import com.starter.lovable.dto.subscription.PlanResponse;
import com.starter.lovable.service.PlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans()
    {
        log.info("PlanServiceImpl.getAllActivePlans called");
        log.debug("PlanServiceImpl.getAllActivePlans returning empty list placeholder");
        return List.of();
    }
}
