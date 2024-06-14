package com.exe.whateat.application.authentication;

import com.exe.whateat.application.account.verification.AccountVerificationService;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public final class RegenerateVerificationCode {

    @Getter
    @Setter
    public static final class RegenerateVerificationCodeRequest {

        @NotBlank(message = "Email là bắt buộc.")
        @Email(message = "Email phải có cấu trúc hợp lệ.")
        private String email;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "auth",
            description = "APIs for authentication/authorization."
    )
    public static class RegenerateVerificationCodeController extends AbstractController {

        private RegenerateVerificationCodeService regenerateVerificationCodeService;

        @PostMapping("/auth/verification/resend")
        @Operation(
                summary = "An API for re-send code for verifying account with account id"
        )
        @ApiResponse(
                description = "Successfully resend through email.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed resending.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> regenerateCode(@RequestBody @Valid RegenerateVerificationCodeRequest request) {
            regenerateVerificationCodeService.regenerateCode(request);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class RegenerateVerificationCodeService {

        private final AccountRepository accountRepository;
        private final AccountVerificationService accountVerificationService;

        public void regenerateCode(RegenerateVerificationCodeRequest request) {
            var account = accountRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0010)
                            .reason("account", "Tài khoản với email không tồn tại.")
                            .build());
            if (account.getStatus() != ActiveStatus.PENDING) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0011)
                        .reason("account", "Tài khoản không hợp lệ để gửi mã xác thực.")
                        .build();
            }
            accountVerificationService.resendVerificationCode(account);
        }
    }
}
