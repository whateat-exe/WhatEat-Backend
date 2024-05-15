package com.exe.whateat.application.authentication;

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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DoLogin {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 32;

    @Data
    @Builder
    public static final class LoginRequest {

        @NotBlank(message = "Email là bắt buộc.")
        @Email(message = "Email phải đúng format của một email.")
        private String email;

        @NotBlank(message = "Mật khẩu là bắt buộc.")
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = "Mật khẩu phải trong khoảng từ 8 tới 32 kí tự.")
        private String password;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "auth",
            description = "APIs for authentication/authorization."
    )
    public static final class DoLoginController extends AbstractController {

        private final DoLoginService service;

        @PostMapping("/auth/login")
        @Operation(
                summary = "Login API. Returns JWT token and refresh for further authentication.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Email and password.",
                        content = @Content(schema = @Schema(implementation = LoginRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful login. Returns JWT token and refresh token.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = TokenResponse.class))
        )
        @ApiResponse(
                description = "Failed login.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> login(@RequestBody @Valid LoginRequest request) {
            final TokenResponse response = service.validateAndReturnTokens(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class DoLoginService {

        private final WhatEatJwtHelper jwtHelper;
        private final AuthenticationManager authenticationManager;

        public TokenResponse validateAndReturnTokens(LoginRequest request) {
            final UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
            final Authentication authentication = authenticationManager.authenticate(authenticationToken);
            final Object principal = authentication.getPrincipal();
            if (!(principal instanceof Account account)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0002)
                        .reason("account", "Unknown account being authenticated")
                        .build();
            }
            final String token = jwtHelper.generateToken(account);
            final String refreshToken = jwtHelper.generateRefreshToken(account);
            return new TokenResponse(token, refreshToken);
        }
    }
}
