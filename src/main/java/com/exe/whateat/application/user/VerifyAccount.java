package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.AccountVerify;
import com.exe.whateat.entity.account.QAccountVerify;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.querydsl.jpa.impl.JPAQuery;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VerifyAccount {

    @Getter
    @Setter
    public static class VerifyAccountRequest {

        @NotBlank(message = "Code is required")
        private String verifyCode;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "An API for verify code"
    )
    public static class VerifyAccountController extends AbstractController {

        private VerifyAccountService verifyAccountService;

        @Operation(
                summary = "An API for Verify account"
        )
        @ApiResponse(
                description = "Successfully verify.",
                responseCode = "200"
        )
        @ApiResponse(
                description = "Failed verifying.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @PatchMapping("/users/{id}/verify")
        public ResponseEntity<Object> verifyAccount(@PathVariable Tsid id, @RequestBody VerifyAccountRequest verifyCode) {
            var result = verifyAccountService.verfiAccount(id, verifyCode);
            if (result)
                return ResponseEntity.ok("Xác thực thành công");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Xác thực không thành công vì code quá hạn hoặc sai code");
        }
    }

    @Service
    @AllArgsConstructor
    public static class VerifyAccountService {

        private EntityManager entityManager;
        private AccountRepository accountRepository;

        public boolean verfiAccount(Tsid id, VerifyAccountRequest verifyCode) {
            final QAccountVerify qAccountverify = QAccountVerify.accountVerify;
            final JPAQuery<AccountVerify> accountVerifyJPAQuery = new JPAQuery<>(entityManager);
            var accountVerifyQuery = accountVerifyJPAQuery
                    .select(qAccountverify)
                    .from(qAccountverify)
                    .where(qAccountverify.account.id.eq(WhatEatId.builder().id(id).build()));
            AccountVerify accountVerify = accountVerifyQuery.fetchFirst();
            long minutes = Duration.between(accountVerify.getLastModified(), Instant.now()).toMinutes();
            if (minutes > 15) {
                return false;
            } else {
                if (accountVerify.getVerifiedCode().equals(verifyCode.verifyCode)) {
                    var account = accountRepository.findById(WhatEatId.builder().id(id).build());
                    if (account.isPresent()) {
                        account.get().setStatus(ActiveStatus.ACTIVE);
                        accountRepository.saveAndFlush(account.get());
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("lỗi server", "Lỗi backend")
                    .build();
        }
    }
}
