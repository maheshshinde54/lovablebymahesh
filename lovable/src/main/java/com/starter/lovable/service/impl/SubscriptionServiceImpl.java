package com.starter.lovable.service.impl;

import com.starter.lovable.dto.subscription.CheckoutRequest;
import com.starter.lovable.dto.subscription.CheckoutResponse;
import com.starter.lovable.dto.subscription.PortalResponse;
import com.starter.lovable.dto.subscription.SubscriptionResponse;
import com.starter.lovable.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId)
    {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId)
    {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId)
    {
        return null;
    }
}
