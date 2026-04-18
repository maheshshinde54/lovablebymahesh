package com.starter.lovable.service;

import com.starter.lovable.dto.subscription.CheckoutRequest;
import com.starter.lovable.dto.subscription.CheckoutResponse;
import com.starter.lovable.dto.subscription.PortalResponse;
import com.starter.lovable.dto.subscription.SubscriptionResponse;
import com.starter.lovable.enums.SubscriptionStatus;

import java.time.Instant;


public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();


    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String id, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String id);

    void renewSubscriptionPeriod(String subId, Instant periodStrat, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();
}





