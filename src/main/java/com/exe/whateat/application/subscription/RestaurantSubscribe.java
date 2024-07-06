package com.exe.whateat.application.subscription;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.subscription.response.SubscriptionPaymentLinkResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.payos.request.PayOSPaymentResponse;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestaurantSubscribe {

    @Data
    @NoArgsConstructor
    public static final class RestaurantSubscribeRequest {

        @NotNull(message = "Loại gói là bắt buộc.")
        private RestaurantSubscriptionType type;
    }

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "subscription",
            description = "APIs for subscription."
    )
    public static final class RestaurantSubscribeController extends AbstractController {

        private final RestaurantSubscribeService service;

        @PostMapping("/subscriptions/restaurants")
        @Operation(
                summary = "Restaurant subscription API. Returns the payment links.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Type of subscription.",
                        content = @Content(schema = @Schema(implementation = RestaurantSubscribeRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation of subscription request. Returns payment links.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = SubscriptionPaymentLinkResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the subscription request.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> subscribe(@RequestBody @Valid RestaurantSubscribeRequest request) {
            final SubscriptionPaymentLinkResponse response = service.subscribe(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @RequiredArgsConstructor
    public static class RestaurantSubscribeService {

        private final SubscriptionService subscriptionService;
        private final WhatEatSecurityHelper securityHelper;
        private final WhatEatMapper<PayOSPaymentResponse, SubscriptionPaymentLinkResponse> mapper;

        public SubscriptionPaymentLinkResponse subscribe(RestaurantSubscribeRequest request) {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0002)
                            .reason("account", "Không xác định được tài khoản.")
                            .build());
            final Restaurant restaurant = account.getRestaurant();
            if (restaurant == null) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0002)
                        .reason("restaurant", "Tài khoản không là nhà hàng.")
                        .build();
            }
            final PayOSPaymentResponse payOSPaymentResponse = subscriptionService.subscribeRestaurant(restaurant,
                    request.getType());
            return mapper.convertToDto(payOSPaymentResponse);
        }
    }
}
