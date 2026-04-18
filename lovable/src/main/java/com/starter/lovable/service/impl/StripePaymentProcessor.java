package com.starter.lovable.service.impl;

import com.starter.lovable.dto.subscription.CheckoutRequest;
import com.starter.lovable.dto.subscription.CheckoutResponse;
import com.starter.lovable.dto.subscription.PortalResponse;
import com.starter.lovable.entity.Plan;
import com.starter.lovable.entity.User;
import com.starter.lovable.enums.SubscriptionStatus;
import com.starter.lovable.error.BadRequestException;
import com.starter.lovable.error.ResourceNotFoundException;
import com.starter.lovable.respository.PlanRepository;
import com.starter.lovable.respository.UserRepository;
import com.starter.lovable.security.AuthUtil;
import com.starter.lovable.service.PaymentProcessor;
import com.starter.lovable.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    @Value("${client.url}")
    private String frontEndUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request)
    {
        Plan plan = planRepository.findById(request.planId())
                                  .orElseThrow(() -> new ResourceNotFoundException("Plan", request.planId()
                                                                                                  .toString()));
        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);
        System.out.println("DEBUG: Using Stripe Price ID: " + plan.getStripePriceId());
        var params = SessionCreateParams.builder()
                                        .addLineItem(SessionCreateParams.LineItem.builder()
                                                                                 .setPrice(plan.getStripePriceId())
                                                                                 .setQuantity(1L)
                                                                                 .build())
                                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)

                                        .setSubscriptionData(new SessionCreateParams.SubscriptionData.Builder().setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder()
                                                                                                                                                                               .setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE)
                                                                                                                                                                               .build())
                                                                                                               .build())
                                        .setSuccessUrl(frontEndUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                                        .setCancelUrl(frontEndUrl + "/cancle.html")
                                        .putMetadata("user_id", userId.toString())
                                        .putMetadata("plan_id", plan.getId()
                                                                    .toString());
        try
        {
            String stripeCustomerId = user.getStripeCustomerId();
            if (stripeCustomerId == null || stripeCustomerId.isEmpty())
            {
                params.setCustomerEmail(user.getUsername());
            } else
            {
                params.setCustomer(stripeCustomerId); // stripe customer Id
            }
            Session session = Session.create(params.build()); // making api call to the Stripe Backend
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public PortalResponse openCustomerPortal()
    {
        Long userId = authUtil.getCurrentUserId();
        User user = getUser(userId);
        String stripeCustomerId = user.getStripeCustomerId();
        if (stripeCustomerId == null || stripeCustomerId.isEmpty())
        {
            log.error("User does not have valid strip customer id : {}", userId);
            throw new BadRequestException("User does not have valid strip customer id : " + userId);
        }
        try
        {
            var portalSession = com.stripe.model.billingportal.Session.create(com.stripe.param.billingportal.SessionCreateParams.builder()
                                                                                                                                .setCustomer(stripeCustomerId)
                                                                                                                                .setReturnUrl(frontEndUrl)
                                                                                                                                .build());
            return new PortalResponse(portalSession.getUrl());
        } catch (StripeException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata)
    {
        log.info("Handling Strip event : {}", type);
        switch (type)
        {
            case "checkout.session.completed" ->
                    handleCheckoutSessionCompleted((Session) stripeObject, metadata); //Happened for the first time only

            case "customer.subscription.updated" ->
                    handleCustomerSubscriptionUpdated((Subscription) stripeObject); // When user update/upgrade the subscription/cancle/

            case "customer.subscription.deleted" ->
                    handleCustomerSubscriptionDeleted((Subscription) stripeObject); //Happened when subscription ended,revoked the access

            case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject); //Happened when invoice is paid

            case "invoice.payment_failed" ->
                    handleInvoicePaymentFailed((Invoice) stripeObject); //when invoice is not paid, marked past due

            default -> log.debug("Ingoing the event : {}", type);


        }
    }

    private void handleCheckoutSessionCompleted(Session session, Map<String, String> metadata)
    {
        if (session == null)
        {
            log.error("session object was null");
            return;
        }
        Long userId = Long.valueOf(metadata.get("user_id"));
        Long planId = Long.valueOf(metadata.get("plan_id"));

        String subscriptionId = session.getSubscription();
        String customerId = session.getCustomer();
        User user = getUser(userId);
        if (user.getStripeCustomerId() == null)
        {
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }

        subscriptionService.activateSubscription(userId, planId, subscriptionId, customerId);


    }

    private void handleCustomerSubscriptionUpdated(Subscription subscription)
    {
        if (subscription == null)
        {
            log.error("subscription object was null");
            return;
        }
        SubscriptionStatus status = mapStripeStatusToEnum(subscription.getStatus());
        if (status == null)
        {
            log.warn("Unknown status '{}' for subscription {}", subscription.getStatus(), subscription.getId());
            return;
        }

        SubscriptionItem item = subscription.getItems()
                                            .getData()
                                            .get(0);
        Instant periodStart = toInstant(item.getCurrentPeriodStart());
        Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

        Long planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(subscription.getId(), status, periodStart, periodEnd, subscription.getCancelAtPeriodEnd(), planId);
    }


    private void handleCustomerSubscriptionDeleted(Subscription subscription)
    {
        if (subscription == null)
        {
            log.error("subscription object was null inside handleCustomerSubscriptionDeleted");
            return;
        }
        subscriptionService.cancelSubscription(subscription.getId());
    }

    private void handleInvoicePaid(Invoice invoice)
    {

        String subId = extractSubscriptionId(invoice);
        if (subId == null)
        {
            log.error("invoice object was null inside handleInvoicePaid");
            return;
        }
        try
        {
            Subscription subscription = Subscription.retrieve(subId);
            var item = subscription.getItems()
                                   .getData()
                                   .get(0);

            Instant periodStrat = toInstant(item.getCurrentPeriodStart());
            Instant periodEnd = toInstant(item.getCurrentPeriodEnd());

            subscriptionService.renewSubscriptionPeriod(subId, periodStrat, periodEnd);

        } catch (StripeException e)
        {
            throw new RuntimeException(e);
        }


    }


    private void handleInvoicePaymentFailed(Invoice invoice)
    {
        String subId = extractSubscriptionId(invoice);
        if (subId == null)
        {
            log.error("invoice object was null inside handleInvoicePaymentFailed()");
            return;
        }
        subscriptionService.markSubscriptionPastDue(subId);

    }


    //Utility Methods
    private User getUser(Long userId)
    {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new ResourceNotFoundException("user", userId.toString()));
    }

    private SubscriptionStatus mapStripeStatusToEnum(String status)
    {
        return switch (status)
        {
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRIALING;
            case "past_due", "unpaid", "paused", "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default ->
            {
                log.warn("Unmapped Stripe status: {}", status);
                yield null;
            }
        };
    }

    private Instant toInstant(Long epoch)
    {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;

    }

    private Long resolvePlanId(Price price)
    {
        if (price == null || price.getId() == null)
        {
            return null;
        }
        return planRepository.findByStripePriceId(price.getId())
                             .map(Plan::getId)
                             .orElse(null);
    }

    //@copiedcode
    private String extractSubscriptionId(Invoice invoice)
    {
        var parent = invoice.getParent();
        if (parent == null)
            return null;

        var subDetails = parent.getSubscriptionDetails();
        if (subDetails == null)
            return null;
        return subDetails.getSubscription();
    }
}
