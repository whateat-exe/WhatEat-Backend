package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UserResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetCurrentUser {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class GetCurrentUserController {

        @RestController
        @AllArgsConstructor
        @Tag(
                name = "user",
                description = "APIs for user accounts."
        )
        public static class GetUserController extends AbstractController {

            private final GetCurrentUserService getCurrentUser;

            @GetMapping("/users/current")
            @Operation(
                    summary = "Get the current user account."
            )
            @ApiResponse(
                    responseCode = "200",
                    description = "Found. Returns info of the user account.",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            )
            @ApiResponse(
                    responseCode = "400s/500s",
                    description = "Can not get the current user",
                    content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
            )
            public ResponseEntity<UserResponse> getAllAccount() {
                final UserResponse response = getCurrentUser.getCurrentUser();
                return ResponseEntity.ok(response);
            }
        }

        @Service
        @AllArgsConstructor
        public static class GetCurrentUserService {

            private final WhatEatSecurityHelper whatEatSecurityHelper;
            private AccountDTOMapper accountDTOMapper;

            public UserResponse getCurrentUser() {

                final Account account = whatEatSecurityHelper.getCurrentLoggedInAccount()
                        .orElseThrow(() -> WhatEatException.builder()
                                .code(WhatEatErrorCode.WES_0002)
                                .reason("account", "Unknown account.")
                                .build());
                return accountDTOMapper.convertToDto(account);
            }
        }
    }
}
