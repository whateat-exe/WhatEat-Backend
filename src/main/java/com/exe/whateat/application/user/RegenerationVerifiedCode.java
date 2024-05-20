package com.exe.whateat.application.user;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.AccountVerify;
import com.exe.whateat.entity.account.QAccountVerify;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.common.GenerationCode;
import com.exe.whateat.infrastructure.email.SendEmailService;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.repository.AccountVerifyRepository;
import com.querydsl.jpa.impl.JPAQuery;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegenerationVerifiedCode {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "regeneration verify code"
    )
    public static class RegenerationCodeController {

        private RegenerationCodeService regenerationCodeService;

        @Operation(
                summary = "An API for re-send code for verifying account with account id"
        )
        @ApiResponse(
                description = "Successfully re-send through email.",
                responseCode = "200"
        )
        @ApiResponse(
                description = "Failed re-sending.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @PatchMapping("/users/{id}/re-send")
        public ResponseEntity<Object> RegenerateCode(@PathVariable Tsid tsid) {
            var result = regenerationCodeService.regenrateCode(tsid);
            if (result)
                return ResponseEntity.ok("Code mới đã được tạo mới và chuyển về mail");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tài khoản đã được kích hoạt");
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class RegenerationCodeService {

        private AccountVerifyRepository accountVerifyRepository;
        private SendEmailService sendEmailService;
        private EntityManager entityManager;
        private AccountRepository accountRepository;

        public boolean regenrateCode(Tsid tsid) {
            var account = accountRepository.findById(WhatEatId.builder().id(tsid).build());
            if(account.isPresent() && account.get().getStatus().equals(ActiveStatus.ACTIVE)) {
                return false;
            }
            else if (account.isPresent() && account.get().getStatus().equals(ActiveStatus.PENDING)) {
                final QAccountVerify qAccountverify = QAccountVerify.accountVerify;
                final JPAQuery<AccountVerify> accountVerifyJPAQuery = new JPAQuery<>(entityManager);
                var accountVerifyQuery = accountVerifyJPAQuery
                        .select(qAccountverify)
                        .from(qAccountverify)
                        .where(qAccountverify.account.id.eq(WhatEatId.builder().id(tsid).build()));
                AccountVerify accountVerify = accountVerifyQuery.fetchFirst();
                var newCOde = GenerationCode.codeGeneration();
                accountVerify.setVerifiedCode(newCOde);
                accountVerifyRepository.saveAndFlush(accountVerify);
                //send mail
                sendEmailService.sendMail(account.get().getEmail(), newCOde, "Resend Code");
                return true;
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("lỗi server", "Lỗi backend")
                    .build();
        }
    }
}
