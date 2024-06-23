package com.exe.whateat.application.user;

import com.exe.whateat.application.account.verification.AccountVerificationService;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminActivateAccount {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for user accounts."
    )
    public static class UserActivateAccountController extends AbstractController {

        private UserActivateAccountService userActivateAccountService;

        @PatchMapping("/users/{id}/active")
        @Operation(
                summary = "An API for activating account"
        )
        @ApiResponse(
                description = "Successfully return the account.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed active.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> activateAccount(@PathVariable Tsid id) {
            userActivateAccountService.activateAccount(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @AllArgsConstructor
    public static class UserActivateAccountService {

        private final AccountRepository accountRepository;
        private final AccountVerificationService accountVerificationService;

        public void activateAccount(Tsid id) {
            Optional<Account> account = accountRepository.findById(WhatEatId.builder().id(id).build());
            if (account.isPresent()) {
                if (account.get().getStatus().equals(ActiveStatus.INACTIVE)) {

                    account.get().setStatus(ActiveStatus.ACTIVE);
                    accountRepository.save(account.get());
                    accountVerificationService.sendActivatingAccountEmail(account.get());
                    return;
                } else if (account.get().getStatus().equals(ActiveStatus.PENDING)) {
                    throw WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEA_0007)
                            .reason("user", "không thể kích hoạt tài khoản chưa được verify")
                            .build();
                }
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEA_0007)
                        .reason("user", "Không thể kích hoạt tài khoản đang được kích hoạt")
                        .build();
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WEA_0007)
                    .reason("server", "Không thể tìm thấy tài khoản này")
                    .build();
        }
    }
}
