package com.exe.whateat.application.personalprofile;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PersonalProfileRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemovePersonalProfiles {

    @Data
    public static final class RemovePersonalProfilesRequest {

        private Set<Tsid> ids = new HashSet<>();
    }

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "profile",
            description = "APIs for profile filters."
    )
    public static final class RemovePersonalProfilesController extends AbstractController {

        private final RemovePersonalProfilesService service;

        @DeleteMapping("/personal-profiles")
        @Operation(
                summary = "Remove profile filters API. Returns nothing. USER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food.",
                        content = @Content(schema = @Schema(implementation = RemovePersonalProfilesRequest.class))
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
        public ResponseEntity<Object> removePersonalProfiles(@RequestBody RemovePersonalProfilesRequest request) {
            service.remove(request);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    @RequiredArgsConstructor
    public static class RemovePersonalProfilesService {

        private final PersonalProfileRepository personalProfileRepository;
        private final WhatEatSecurityHelper securityHelper;

        public void remove(RemovePersonalProfilesRequest request) {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            final Set<Tsid> personalProfileIds = Objects.requireNonNullElse(request.getIds(), new HashSet<>());
            personalProfileRepository.deleteAllByAccountIdAndIdIn(account.getId(), personalProfileIds.stream()
                    .map(WhatEatId::new).toList());
        }
    }
}
