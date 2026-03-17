package com.starter.lovable.service;

import com.starter.lovable.dto.subscription.CheckoutRequest;
import com.starter.lovable.dto.subscription.CheckoutResponse;
import com.starter.lovable.dto.subscription.PortalResponse;
import com.starter.lovable.dto.subscription.SubscriptionResponse;


public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request,
                                              Long userId);

    PortalResponse openCustomerPortal(Long userId);
}


