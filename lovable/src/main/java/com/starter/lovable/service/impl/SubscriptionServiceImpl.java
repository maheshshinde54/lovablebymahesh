package com.starter.lovable.service.impl;


import com.starter.lovable.dto.subscription.SubscriptionResponse;
import com.starter.lovable.entity.Plan;
import com.starter.lovable.entity.Subscription;
import com.starter.lovable.entity.User;
import com.starter.lovable.enums.SubscriptionStatus;
import com.starter.lovable.error.ResourceNotFoundException;
import com.starter.lovable.mapper.SubscriptionMapper;
import com.starter.lovable.respository.*;
import com.starter.lovable.security.AuthUtil;
import com.starter.lovable.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class SubscriptionServiceImpl implements SubscriptionService {

    AuthUtil authUtil;
    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;
    UserRepository userRepository;
    PlanRepository planRepository;
    ProjectRepository projectRepository;
    ProjectMemberRepository projectMemberRepository;
    Integer FREE_TIER_PROJECT_ALLOWED=1;


    @Override
    public SubscriptionResponse getCurrentSubscription()
    {
        Long userId = authUtil.getCurrentUserId();
        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.TRIALING))
                                                        .orElse(new Subscription());

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId)
    {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists)
            return;
        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                                                .user(user)
                                                .plan(plan)
                                                .stripeSubscriptionId(subscriptionId)
                                                .status(SubscriptionStatus.INCOMPLETE)
                                                .build();

        subscriptionRepository.save(subscription);

    }

    @Transactional
    @Override
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart,
                                   Instant periodEnd, Boolean cancelAtPeriod, Long planId)
    {
        boolean hasSubscriptionUpdated = false;

        Subscription subscription = getSubscription(gatewaySubscriptionId);
        if (status != null && status != subscription.getStatus())
        {
            subscription.setStatus(status);
            hasSubscriptionUpdated = true;
        }
        if (periodStart != null && periodStart != subscription.getCurrentPeriodStart())
        {
            subscription.setCurrentPeriodStart(periodStart);
            hasSubscriptionUpdated = true;

        }
        if (periodEnd != null && periodEnd != subscription.getCurrentPeriodEnd())
        {
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionUpdated = true;

        }
        if (cancelAtPeriod != null && cancelAtPeriod != subscription.getCancelAtPeriodEnd())
        {
            subscription.setCancelAtPeriodEnd(cancelAtPeriod);
            hasSubscriptionUpdated = true;

        }
        if (planId != null && planId.equals(subscription.getPlan()
                                                        .getId()))
        {
            Plan plan = getPlan(planId);
            subscription.setPlan(plan);
            hasSubscriptionUpdated = true;

        }
        if (hasSubscriptionUpdated)
        {
            log.info("Subscription has been updated: {}", subscription);
            subscriptionRepository.save(subscription);

        }



    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId)
    {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);

    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStrat, Instant periodEnd)
    {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        Instant newStart = periodStrat != null ? periodStrat : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE)
        {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        subscriptionRepository.save(subscription);

    }


    @Override
    public void markSubscriptionPastDue(String subscriptionId)
    {
        Subscription subscription = getSubscription(subscriptionId);
        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE)
        {
            log.info("Subscription is already past due");
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);
    }

    @Override
    public boolean canCreateNewProject()
    {
        Long userId= authUtil.getCurrentUserId();
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        int countOfOwnerProject = projectMemberRepository.countProjectOwnedByUser(userId);
        if(currentSubscription.plan()==null)
        {
            return countOfOwnerProject<FREE_TIER_PROJECT_ALLOWED;
        }
        return countOfOwnerProject<currentSubscription.plan().maxProjects();
    }

    //Utility Methods

    private User getUser(Long userId)
    {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId)
    {
        return planRepository.findById(planId)
                             .orElseThrow(() -> new ResourceNotFoundException("Plan", planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId)
    {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                                     .orElseThrow(() -> new ResourceNotFoundException("GatewaySubscription", gatewaySubscriptionId));
    }


}
