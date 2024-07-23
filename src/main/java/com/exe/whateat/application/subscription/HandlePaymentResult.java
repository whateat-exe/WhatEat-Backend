package com.exe.whateat.application.subscription;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.subscription.request.PayOSPaymentReturnResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RequestCreateTracker;
import com.exe.whateat.entity.request.RequestCreateTrackerStatus;
import com.exe.whateat.entity.subscription.PaymentStatus;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import com.exe.whateat.entity.subscription.SubscriptionStatus;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import com.exe.whateat.infrastructure.repository.RequestCreateTrackerRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
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
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HandlePaymentResult {

    private static final String SUCCESSFUL_CODE = "00";

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "subscription",
            description = "APIs for subscription."
    )
    public static final class HandlePaymentResultController extends AbstractController {

        private final HandlePaymentResultService service;

        @GetMapping(value = "/subscriptions/payos")
        @Operation(
                summary = "API for PayOS to send back response to. DO NO USE!",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Type of subscription.",
                        content = @Content(schema = @Schema(implementation = PayOSPaymentReturnResponse.class))
                )
        )
        @ApiResponse(
                description = "Successful validation. Returns thank you page.",
                responseCode = "200",
                content = @Content(mediaType = "text/html")
        )
        @ApiResponse(
                description = "Failed to validate response.",
                responseCode = "400s/500s",
                content = @Content(mediaType = "text/html")
        )
        public ResponseEntity<Object> handleSuccessPayment(@ParameterObject PayOSPaymentReturnResponse response)
                throws IOException {
            try {
                service.handle(response);
                final ClassPathResource resource = new ClassPathResource("static/html/success.html");
                final String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_HTML);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .headers(headers)
                        .body(html);
            } catch (Exception e) {
                final ClassPathResource resource = new ClassPathResource("static/html/error.html");
                final String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_HTML);
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .headers(headers)
                        .body(html);
            }
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @RequiredArgsConstructor
    @SuppressWarnings("Duplicates")
    public static class HandlePaymentResultService {

        private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
        private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;
        private final RequestCreateTrackerRepository requestCreateTrackerRepository;
        private final RestaurantRepository restaurantRepository;

        public void handle(PayOSPaymentReturnResponse response) {
            if (!StringUtils.equals(response.getCode(), SUCCESSFUL_CODE)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("payment", "Lỗi thanh toán")
                        .build();
            }
            if (response.isCancel()) {
                handleCancelPayment(response);
                return;
            }

            if (StringUtils.equals(response.getCode(), SUCCESSFUL_CODE)) {
                final Optional<RestaurantSubscriptionTracker> restaurantSubscriptionTracker =
                        restaurantSubscriptionTrackerRepository.findByPaymentIdAndOrderCode(response.getId(),
                                response.getOrderCode());
                if (restaurantSubscriptionTracker.isPresent()) {
                    handleCreateRequestDishRestaurantTracker(restaurantSubscriptionTracker.get().getRestaurant().getId(), restaurantSubscriptionTracker.get().getSubscription());
                }
            }
        }

        private void handleCancelPayment(PayOSPaymentReturnResponse response) {
            final Optional<RestaurantSubscriptionTracker> restaurantSubscriptionTracker =
                    restaurantSubscriptionTrackerRepository.findByPaymentIdAndOrderCode(response.getId(),
                            response.getOrderCode());
            if (restaurantSubscriptionTracker.isPresent()) {
                final RestaurantSubscriptionTracker restaurantSubscriptionTrack = restaurantSubscriptionTracker.get();
                if (restaurantSubscriptionTrack.getPaymentStatus() != PaymentStatus.PENDING
                        || restaurantSubscriptionTrack.getSubscriptionStatus() != SubscriptionStatus.PENDING) {
                    throw WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("payment", "PayOS response không hợp lệ")
                            .build();
                }
                restaurantSubscriptionTrack.setPaymentStatus(PaymentStatus.CANCELLED);
                restaurantSubscriptionTrack.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
                restaurantSubscriptionTrackerRepository.save(restaurantSubscriptionTrack);
                return;
            }
            final Optional<UserSubscriptionTracker> userSubscriptionTracker =
                    userSubscriptionTrackerRepository.findByPaymentIdAndOrderCode(response.getId(),
                            response.getOrderCode());
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
            userSubscriptionTrack.setPaymentStatus(PaymentStatus.CANCELLED);
            userSubscriptionTrack.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
            userSubscriptionTrackerRepository.save(userSubscriptionTrack);
        }

        private void handleCreateRequestDishRestaurantTracker(WhatEatId restaurantId, RestaurantSubscription restaurantSubscription) {
            int maxNumberOfCreatingDish = 0;
            if (restaurantSubscription.getType().equals(RestaurantSubscriptionType.SILVER))
                maxNumberOfCreatingDish = 10;
            else if (restaurantSubscription.getType().equals(RestaurantSubscriptionType.GOLD))
                maxNumberOfCreatingDish = 30;
            else if (restaurantSubscription.getType().equals(RestaurantSubscriptionType.DIAMOND))
                maxNumberOfCreatingDish = 50;
            RequestCreateTracker requestCreateTracker =
                    RequestCreateTracker
                            .builder()
                            .id(WhatEatId.generate())
                            .numberOfRequestedDish(0)
                            .requestCreateTrackerStatus(RequestCreateTrackerStatus.ACTIVE)
                            .validityStart(Instant.now())
                            .validityEnd(Instant.now().plus(30, ChronoUnit.DAYS))
                            .maxNumberOfCreateDish(maxNumberOfCreatingDish)
                            .restaurant(restaurantRepository.getReferenceById(restaurantId))
                            .build();
            requestCreateTrackerRepository.save(requestCreateTracker);
        }
    }
}