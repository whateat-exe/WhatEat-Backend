package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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
                description = "APIs for get current users"
        )
        public static class GetUserController extends AbstractController {

            private final GetCurrentUserService getCurrentUser;

            @GetMapping("/users/current")
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
