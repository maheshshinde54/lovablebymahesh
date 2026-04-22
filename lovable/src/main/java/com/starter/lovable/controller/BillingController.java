package com.starter.lovable.controller;

import com.starter.lovable.dto.subscription.*;
import com.starter.lovable.service.PaymentProcessor;
import com.starter.lovable.service.PlanService;
import com.starter.lovable.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing")
public class BillingController {
    private final PlanService planService;
    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;

    @Value("${webhook-signing-secret}")
    private String webhookSecret;

    @GetMapping("/plans")
    public ResponseEntity<List<PlanResponse>> getAllPlans()
    {
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/me/subscriptions")
    public ResponseEntity<SubscriptionResponse> getMySubscription()
    {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PostMapping("/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(@RequestBody CheckoutRequest request)
    {
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(request));
    }

    @PostMapping("/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal()
    {

        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }

    @PostMapping("/webhooks/payment")
    public ResponseEntity<String> handlePaymentWebhooks(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signHeader)
    {
        log.info("BillingController.handlePaymentWebhooks called");
        log.debug("Stripe webhook raw payload: {}", payload);

        try {
            Event event = Webhook.constructEvent(payload, signHeader, webhookSecret);
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (deserializer.getObject().isPresent()) {
                stripeObject = deserializer.getObject().get();
            } else {
                try {
                    stripeObject = deserializer.deserializeUnsafe();
                    if (stripeObject == null) {
                        log.warn("BillingController.handlePaymentWebhooks failed to deserialize event {}", event.getType());
                        return ResponseEntity.ok().build();
                    }
                } catch (Exception e) {
                    log.error("BillingController.handlePaymentWebhooks unsafe deserialization failed for event {}: {}", event.getType(), e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
                }
            }

            Map<String, String> metadata = new HashMap<>();
            if (stripeObject instanceof Session session) {
                metadata = session.getMetadata();
            }

            log.info("BillingController.handlePaymentWebhooks dispatching event={}", event.getType());
            paymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metadata);
            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            log.error("BillingController.handlePaymentWebhooks signature verification failed");
            log.debug("Stripe signature header: {}", signHeader);
            log.debug("Webhook signing secret length: {}", webhookSecret != null ? webhookSecret.length() : 0);
            throw new RuntimeException(e);
        }
    }

}
