package com.exe.whateat.application.subscription;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.subscription.response.SubscriptionPaymentLinkResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.payos.request.PayOSPaymentResponse;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSubscribe {

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "subscription",
            description = "APIs for subscription."
    )
    public static final class UserSubscribeController extends AbstractController {

        private final UserSubscribeService service;

        @PostMapping("/subscriptions/users")
        @Operation(
                summary = "User subscription API. Returns the payment links."
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
        public ResponseEntity<Object> userSubscribe() {
            final SubscriptionPaymentLinkResponse response = service.subscribe();
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @RequiredArgsConstructor
    public static class UserSubscribeService {

        private final WhatEatSecurityHelper securityHelper;
        private final SubscriptionService subscriptionService;
        private final WhatEatMapper<PayOSPaymentResponse, SubscriptionPaymentLinkResponse> mapper;

        public SubscriptionPaymentLinkResponse subscribe() {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0002)
                            .reason("account", "Không xác định được tài khoản.")
                            .build());
            final PayOSPaymentResponse payOSPaymentResponse = subscriptionService.subscribeUser(account);
            return mapper.convertToDto(payOSPaymentResponse);
        }
    }
}
