package com.exe.whateat.application.subscription;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackerResponse;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackersResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackerResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackersResponse;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckSubscriptionByPaymentId {

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "subscription",
            description = "APIs for subscription."
    )
    public static final class CheckSubscriptionByPaymentIdController extends AbstractController {

        private final CheckSubscriptionByPaymentIdService service;

        @GetMapping("/subscriptions/paymentId/{paymentId}")
        @Operation(
                summary = "Check if Subscription is success by paymentId"
        )
        @ApiResponse(
                description = "Successful.",
                responseCode = "200",
                content = {
                        @Content(schema = @Schema(implementation = RestaurantSubscriptionTrackersResponse.class)),
                        @Content(schema = @Schema(implementation = UserSubscriptionTrackersResponse.class))
                }
        )
        @ApiResponse(
                description = "Failed.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> get(@PathVariable String paymentId) {
            final Object response = service.get(paymentId);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @RequiredArgsConstructor
    public static class CheckSubscriptionByPaymentIdService {

        private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;
        private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
        private final WhatEatMapper<UserSubscriptionTracker, UserSubscriptionTrackerResponse> userMapper;
        private final WhatEatMapper<RestaurantSubscriptionTracker, RestaurantSubscriptionTrackerResponse> restaurantMapper;

        public Object get(String paymentId) {
            if (userSubscriptionTrackerRepository.existsByPaymentId(paymentId)) {
                UserSubscriptionTracker tracker = userSubscriptionTrackerRepository.findByPaymentId(paymentId);
                return userMapper.convertToDto(tracker);
            }
            if (restaurantSubscriptionTrackerRepository.existsByPaymentId(paymentId)) {
                RestaurantSubscriptionTracker tracker = restaurantSubscriptionTrackerRepository.findByPaymentId(paymentId);
                return restaurantMapper.convertToDto(tracker);
            }
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEB_0022)
                    .reason("subscription", "Không xác định được gói.")
                    .build();
        }

    }

}
