package com.exe.whateat.application.subscription;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RequestCreateTracker;
import com.exe.whateat.entity.request.RequestCreateTrackerStatus;
import com.exe.whateat.entity.subscription.PaymentStatus;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import com.exe.whateat.entity.subscription.SubscriptionStatus;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RequestCreateTrackerRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HandleSubscriptionWebhook {

    private static final String PAYOS_PAYMENT_LINK_ID = "124c33293c43417ab7879e14c8d9eb18";

    private static final int PAYOS_ORDER_CODE = 123;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class WebhookRequest {

        @lombok.Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Data {
            private int orderCode;
            private int amount;
            private String description;
            private String accountNumber;
            private String reference;
            private String transactionDateTime;
            private String currency;
            private String paymentLinkId;
            private String code;
            private String desc;
            private String counterAccountBankId;
            private String counterAccountBankName;
            private String counterAccountName;
            private String counterAccountNumber;
            private String virtualAccountName;
            private String virtualAccountNumber;
        }

        private String code;
        private String desc;
        private Data data;
        private String signature;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class WebhookResponse {

        private boolean success;
    }

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "subscription",
            description = "APIs for subscription."
    )
    public static final class HandleSubscriptionWebhookController extends AbstractController {

        private final HandleSubscriptionWebhookService service;

        @PostMapping("/subscriptions/webhook")
        @Operation(
                summary = "Subscription webhook API. DO NO USE!",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Type of subscription.",
                        content = @Content(schema = @Schema(implementation = WebhookRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful validation of payment. Returns success = true.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = WebhookResponse.class))
        )
        @ApiResponse(
                description = "Failed validation.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> handleSubscriptionWebhook(@RequestBody WebhookRequest request) {
            if (request.getData() == null) {
                // Skip if data is null
                return ResponseEntity.ok(new WebhookResponse(true));
            }
            if (StringUtils.equals(request.getData().getPaymentLinkId(), PAYOS_PAYMENT_LINK_ID)
                    && request.getData().getOrderCode() == PAYOS_ORDER_CODE) {
                // Sample data from PayOS so skip
                return ResponseEntity.ok(new WebhookResponse(true));
            }
            service.handle(request);
            return ResponseEntity.ok(new WebhookResponse(true));
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @RequiredArgsConstructor
    public static class HandleSubscriptionWebhookService {

        private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
        private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;

        @SuppressWarnings("Duplicates")
        public void handle(WebhookRequest request) {
            final Optional<RestaurantSubscriptionTracker> restaurantSubscriptionTracker =
                    restaurantSubscriptionTrackerRepository.findByPaymentIdAndOrderCode(request.getData().getPaymentLinkId(),
                            request.getData().getOrderCode());
            final Instant validityStart = Instant.now();
            final Instant validityEnd = validityStart.plus(30, ChronoUnit.DAYS);
            if (restaurantSubscriptionTracker.isPresent()) {
                final RestaurantSubscriptionTracker restaurantSubscriptionTrack = restaurantSubscriptionTracker.get();
                if (restaurantSubscriptionTrack.getPaymentStatus() != PaymentStatus.PENDING
                        || restaurantSubscriptionTrack.getSubscriptionStatus() != SubscriptionStatus.PENDING) {
                    throw WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("payment", "PayOS response không hợp lệ")
                            .build();
                }
                restaurantSubscriptionTrack.setPaymentStatus(PaymentStatus.PAID);
                restaurantSubscriptionTrack.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
                restaurantSubscriptionTrack.setValidityStart(validityStart);
                restaurantSubscriptionTrack.setValidityEnd(validityEnd);
                restaurantSubscriptionTrackerRepository.cancelAllCurrentlyActiveSubscriptions(
                        restaurantSubscriptionTrack.getRestaurant().getId(), validityEnd);
                restaurantSubscriptionTrackerRepository.save(restaurantSubscriptionTrack);
                return;
            }
            final Optional<UserSubscriptionTracker> userSubscriptionTracker =
                    userSubscriptionTrackerRepository.findByPaymentIdAndOrderCode(request.getData().getPaymentLinkId(),
                            request.getData().getOrderCode());
            if (userSubscriptionTracker.isEmpty()) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("payment", "PayOS response không hợp lệ")
                        .build();
            }
            final UserSubscriptionTracker userSubscriptionTrack = userSubscriptionTracker.get();
            if (userSubscriptionTrack.getPaymentStatus() != PaymentStatus.PENDING
                    || userSubscriptionTrack.getSubscriptionStatus() != SubscriptionStatus.PENDING) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("payment", "PayOS response không hợp lệ")
                        .build();
            }
            userSubscriptionTrack.setPaymentStatus(PaymentStatus.PAID);
            userSubscriptionTrack.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            userSubscriptionTrack.setValidityStart(validityStart);
            userSubscriptionTrack.setValidityEnd(validityEnd);
            userSubscriptionTrackerRepository.cancelAllCurrentlyActiveSubscriptions(
                    userSubscriptionTrack.getUser().getId(), validityEnd);
            userSubscriptionTrackerRepository.save(userSubscriptionTrack);
        }
    }
}
