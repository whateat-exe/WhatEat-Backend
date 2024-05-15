package com.exe.whateat.application.user;

import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatRegex;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUser {

    @Data
    public static class UpdateUserRequest {
        private String email;
        private String fullName;
        private String phoneNumber;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for update a user with Id"
    )
    public static final class UpdateUserController extends AbstractController {

        private final UpdateUserService updateUserService;
        private final WhatEatSecurityHelper whatEatSecurityHelper;

        @PatchMapping("/users/{id}")
        public ResponseEntity<UserResponse> updateUser(
                @RequestBody UpdateUserRequest updateUserRequest, @PathVariable String id
        ) {
            Optional<Account> account = whatEatSecurityHelper.getCurrentLoggedInAccount();
            if (account.isEmpty()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEA_0003)
                        .reason("Unauthenticated", "You have not logged in")
                        .build();
            }
            UserResponse userResponse = updateUserService.updateUser(updateUserRequest, id);
            return ResponseEntity.ok(userResponse);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class UpdateUserService {

        private final AccountRepository accountRepository;
        private final AccountDTOMapper accountDTOMapper;

        public UserResponse updateUser(UpdateUserRequest updateUserRequest, String id) {

            WhatEatId whatEatId = WhatEatId.builder().id(Tsid.fromString(id)).build();
            Optional<Account> accountExisting = accountRepository.findById(whatEatId);
            if (accountExisting.isPresent()) {
                if (!updateUserRequest.email.isBlank()) {
                    if (WhatEatRegex.checkPattern(WhatEatRegex.emailPattern, updateUserRequest.email))
                        accountExisting.get().setEmail(updateUserRequest.email);
                    else
                        throw WhatEatException
                                .builder()
                                .code(WhatEatErrorCode.WEV_0001)
                                .reason("email", "Invalid email address")
                                .build();
                }

                if (!updateUserRequest.phoneNumber.isBlank()) {
                    if (WhatEatRegex.checkPattern(WhatEatRegex.phonePattern, updateUserRequest.phoneNumber))
                        accountExisting.get().setPhoneNumber(updateUserRequest.getPhoneNumber());
                    else
                        throw WhatEatException
                                .builder()
                                .code(WhatEatErrorCode.WEV_0001)
                                .reason("email", "Invalid email address")
                                .build();
                }

                if (!updateUserRequest.fullName.isBlank()) {
                    accountExisting.get().setFullName(updateUserRequest.getFullName());
                }

                Account accountUpdated = accountRepository.save(accountExisting.get());
                return accountDTOMapper.convertToDto(accountUpdated);
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WEA_0007)
                    .reason("server", "Fault in internal server")
                    .build();
        }
    }

}
