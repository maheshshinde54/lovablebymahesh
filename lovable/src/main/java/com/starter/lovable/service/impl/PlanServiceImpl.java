package com.starter.lovable.service.impl;

import com.starter.lovable.dto.subscription.PlanResponse;
import com.starter.lovable.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans()
    {
        return List.of();
    }
}
