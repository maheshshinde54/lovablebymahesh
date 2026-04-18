package com.starter.lovable.mapper;

import com.starter.lovable.dto.subscription.PlanResponse;
import com.starter.lovable.dto.subscription.SubscriptionResponse;
import com.starter.lovable.entity.Plan;
import com.starter.lovable.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);

}
