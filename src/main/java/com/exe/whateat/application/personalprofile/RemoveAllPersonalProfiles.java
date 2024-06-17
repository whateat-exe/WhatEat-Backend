package com.exe.whateat.application.personalprofile;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PersonalProfileRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemoveAllPersonalProfiles {

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "profile",
            description = "APIs for profile filters."
    )
    public static final class RemoveAllPersonalProfilesController extends AbstractController {

        private final RemoveAllPersonalProfilesService service;

        @DeleteMapping("/personal-profiles/all")
        @Operation(
                summary = "Remove profile filters API. Returns nothing. USER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food.",
                        content = @Content(schema = @Schema(implementation = RemovePersonalProfiles.RemovePersonalProfilesRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful removal.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed removal of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> removeAllPersonalProfiles() {
            service.remove();
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    @RequiredArgsConstructor
    public static class RemoveAllPersonalProfilesService {

        private final PersonalProfileRepository repository;
        private final WhatEatSecurityHelper securityHelper;

        public void remove() {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            repository.deleteAllByAccountId(account.getId());
        }
    }
}
