package com.exe.whateat.application.user;

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
public class ActiveAccount {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "active a user"
    )
    public static class ActiveAccountController extends AbstractController {

        private ActiveUserService activeUserService;
        @Operation(
                summary = "An API for active account"
        )
        @ApiResponse(
                description = "Successfully return account active.",
                responseCode = "200"
        )
        @ApiResponse(
                description = "Failed active.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @PatchMapping("/users/{id}/active")
        public ResponseEntity<Object> activeAnAccount(@PathVariable Tsid id) {
            activeUserService.activeUser(id);
            return ResponseEntity.ok("Active user with id: " + id + "successfully");
        }
    }

    @Service
    @AllArgsConstructor
    public static class ActiveUserService {

        private final AccountRepository accountRepository;

        public void activeUser(Tsid id) {
            Optional<Account> account = accountRepository.findById(WhatEatId.builder().id(id).build());
            if (account.isPresent()) {
                if (account.get().getStatus().equals(ActiveStatus.INACTIVE)) {

                    account.get().setStatus(ActiveStatus.ACTIVE);
                    accountRepository.save(account.get());
                    return;
                } else if (account.get().getStatus().equals(ActiveStatus.PENDING)) {
                    throw WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEA_0007)
                            .reason("user", "Khôgn thể kích hoạt tài khoản chưa được verify")
                            .build();
                }
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEA_0007)
                        .reason("user", "Không thể kích hoạt tài khoản đang đucợ kích hoạt")
                        .build();
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WEA_0007)
                    .reason("server", "can not find account by that user")
                    .build();
        }
    }
}
