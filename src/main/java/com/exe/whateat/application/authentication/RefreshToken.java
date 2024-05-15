package com.exe.whateat.application.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.exe.whateat.application.authentication.response.TokenResponse;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.security.jwt.WhatEatJwtHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RefreshToken {

    /**
     * Somehow having only one property would make deserialization throws error, so we need to use @NoArgsConstructor.
     * Maybe in the future, all request class must have @NoArgsConstructor instead of @Builder?
     */
    @Data
    @NoArgsConstructor
    public static final class RefreshTokenRequest {

        @NotBlank(message = "Refresh token là bắt buộc.")
        private String refreshToken;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "auth",
            description = "APIs for authentication/authorization."
    )
    public static final class RefreshTokenController extends AbstractController {

        private final RefreshTokenService service;

        @PostMapping("/auth/refresh")
        @Operation(
                summary = "Refresh token API. Returns JWT token and refresh for further authentication.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Refresh token.",
                        content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful refresh. Returns JWT token and refresh token.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = TokenResponse.class))
        )
        @ApiResponse(
                description = "Failed refresh.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
            final TokenResponse response = service.refreshToken(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class RefreshTokenService {

        private final WhatEatJwtHelper jwtHelper;
        private final UserDetailsService userDetailsService;

        public TokenResponse refreshToken(RefreshTokenRequest request) {
            final DecodedJWT jwt = jwtHelper.verifyRefreshToken(request.getRefreshToken());
            final String email = jwtHelper.extractEmail(jwt);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (!userDetails.isEnabled()) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEA_0004)
                        .reason("account", "Account is disabled. Please contact admin for further guidance.")
                        .build();
            }
            if (!(userDetails instanceof Account account)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0002)
                        .reason("account", "Unknown account.")
                        .build();
            }
            final String token = jwtHelper.generateToken(account);
            final String refreshToken = jwtHelper.generateRefreshToken(account);
            return new TokenResponse(token, refreshToken);
        }
    }
}
