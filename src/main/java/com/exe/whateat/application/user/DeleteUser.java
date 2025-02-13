package com.exe.whateat.application.user;

import com.exe.whateat.application.account.verification.AccountVerificationService;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteUser {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for user accounts."
    )
    public static final class DeleteUserController extends AbstractController {

        private final DeleteUserService deleteUserService;
        private final WhatEatSecurityHelper whatEatSecurityHelper;


        @DeleteMapping("/users/{id}")
        @Operation(
                summary = "Delete user account."
        )
        @ApiResponse(
                responseCode = "204",
                description = "Delete user successfully. No content will be returned."
        )
        @ApiResponse(
                responseCode = "400s/500s",
                description = "Can not delete the user",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> deleteUser(@PathVariable String id) {
            Optional<Account> account = whatEatSecurityHelper.getCurrentLoggedInAccount();
            if (account.isEmpty()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEA_0003)
                        .reason("Unauthenticated", "You have not logged in")
                        .build();
            }
            if (account.get().getRole() != AccountRole.ADMIN) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEA_0002)
                        .reason("Forbidden", "You are not privileged to do this function")
                        .build();
            }
            deleteUserService.deleteUser(id);
            return ResponseEntity.ok("Disable account successfully");
        }

    }

    @Service
    @AllArgsConstructor
    public static class DeleteUserService {

        private final AccountRepository accountRepository;
        private final AccountVerificationService accountVerificationService;

        public void deleteUser(String id) {
            Tsid tsid = Tsid.fromString(id);
            WhatEatId whatEatId = WhatEatId.builder().id(tsid).build();
            Account account = accountRepository.findById(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0007)
                            .reason("server", "can not find account by that user")
                            .build());

            if (account.getStatus().equals(ActiveStatus.ACTIVE)) {
                account.setStatus(ActiveStatus.INACTIVE);
                accountRepository.save(account);
                accountVerificationService.sendDeactivatingAccountEmail(account);
            } else {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEA_0007)
                        .reason("server", "Can not disable an inactive account")
                        .build();
            }
        }

    }
}
