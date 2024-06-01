package com.exe.whateat.application.food;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.randomhistory.RandomService;
import com.exe.whateat.application.randomhistory.response.RandomResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckIfAllowedToRandomize {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "random",
            description = "APIs for randomization."
    )
    public static final class CheckIfAllowedToRandomizeController extends AbstractController {

        private final RandomService randomService;
        private final WhatEatSecurityHelper securityHelper;

        @GetMapping("/foods/random/status")
        @Operation(
                summary = "Check if allowed to randomize food API. Returns the random status."
        )
        @ApiResponse(
                description = "Successful. Only for USER.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RandomResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> checkIfAllowedToRandomize() {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            final RandomResponse response = randomService.checkIfAllowedToRandomize(account);
            return ResponseEntity.ok(response);
        }
    }
}
