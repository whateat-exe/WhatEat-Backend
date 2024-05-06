package com.exe.whateat.application.authentication;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.security.jwt.WhatEatJwtHelper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
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
    private static final String INVALID_PASSWORD_LENGTH = String
            .format("Password must be between %d and %d", PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);

    public record LoginRequest(String email, String password) {

        @SneakyThrows
        public LoginRequest(String email, String password) {
            final String trimmedEmail = StringUtils.trim(email);
            if (StringUtils.isBlank(trimmedEmail)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0001)
                        .reason("email", "Email is required")
                        .build();
            }
            if (!EmailValidator.getInstance().isValid(trimmedEmail)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0001)
                        .reason("email", "Email has invalid format")
                        .build();
            }
            final String trimmedPassword = StringUtils.trim(password);
            if (StringUtils.isBlank(trimmedPassword)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0002)
                        .reason("password", "Password is required")
                        .build();
            }
            if (trimmedPassword.length() < PASSWORD_MIN_LENGTH || trimmedPassword.length() > PASSWORD_MAX_LENGTH) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0002)
                        .reason("password", INVALID_PASSWORD_LENGTH)
                        .build();
            }
            this.email = trimmedEmail;
            this.password = trimmedPassword;
        }
    }

    public record LoginResponse(String token, String refreshToken) {

    }

    @RestController
    @AllArgsConstructor
    public static final class DoLoginController extends AbstractController {

        private final DoLoginService service;

        @PostMapping("/auth/login")
        public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
            final LoginResponse response = service.validateAndReturnTokens(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class DoLoginService {

        private final WhatEatJwtHelper jwtHelper;
        private final AuthenticationManager authenticationManager;

        public LoginResponse validateAndReturnTokens(LoginRequest request) {
            final UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(request.email, request.password);
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
            return new LoginResponse(token, refreshToken);
        }
    }
}
