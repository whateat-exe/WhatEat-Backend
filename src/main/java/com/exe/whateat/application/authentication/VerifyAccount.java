package com.exe.whateat.application.authentication;

import com.exe.whateat.application.account.verification.AccountVerificationService;
import com.exe.whateat.application.authentication.response.TokenResponse;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VerifyAccount {

    @Getter
    @Setter
    public static class VerifyAccountRequest {

        @NotBlank(message = "Email là bắt buộc.")
        @Email(message = "Email phải có cấu trúc hợp lệ.")
        private String email;

        @NotBlank(message = "Mã xác thực là bắt buộc.")
        @Size(min = 6, max = 6, message = "Mã xác thực phải có 6 chữ số.")
        private String verificationCode;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for user accounts."
    )
    public static class VerifyAccountController extends AbstractController {

        private VerifyAccountService verifyAccountService;

        @PostMapping("/auth/verification")
        @Operation(
                summary = "An API for verifying account"
        )
        @ApiResponse(
                description = "Successfully verification. Returns authentication token and refresh token.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = TokenResponse.class))
        )
        @ApiResponse(
                description = "Failed verification.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> verifyAccount(@RequestBody @Valid VerifyAccountRequest verifyCode) {
            var result = verifyAccountService.verifyAccount(verifyCode);
            return ResponseEntity.ok(result);
        }
    }

    @Service
    @AllArgsConstructor
    public static class VerifyAccountService {

        private final AccountRepository accountRepository;
        private final AccountVerificationService accountVerificationService;
        private final WhatEatJwtHelper jwtHelper;

        public TokenResponse verifyAccount(VerifyAccountRequest request) {
            final Account account = accountRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0010)
                            .reason("account", "Tài khoản với email không tồn tại.")
                            .build());
            accountVerificationService.verifyAccount(account, request.getVerificationCode());
            final String token = jwtHelper.generateToken(account);
            final String refreshToken = jwtHelper.generateRefreshToken(account);
            return new TokenResponse(token, refreshToken);
        }
    }
}
