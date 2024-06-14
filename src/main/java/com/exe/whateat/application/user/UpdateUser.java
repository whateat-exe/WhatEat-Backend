package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatRegex;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
        private String image;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for user accounts."
    )
    public static final class UpdateUserController extends AbstractController {

        private final UpdateUserService updateUserService;
        private final WhatEatSecurityHelper whatEatSecurityHelper;

        @PatchMapping("/users/{id}")
        @Operation(
                summary = "Update user account.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the user.",
                        content = @Content(schema = @Schema(implementation = UpdateUserRequest.class))
                )
        )
        @ApiResponse(
                responseCode = "204",
                description = "Create a new user successfully. No content will be returned."
        )
        @ApiResponse(
                responseCode = "400s/500s",
                description = "Can not create a new user",
                content = @Content(schema = @Schema(implementation = UserResponse.class))
        )
        public ResponseEntity<UserResponse> updateUser(
                @RequestBody UpdateUserRequest updateUserRequest,
                @PathVariable String id) {
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
    @Transactional(rollbackOn = Exception.class)
    public static class UpdateUserService {

        private final AccountRepository accountRepository;
        private final AccountDTOMapper accountDTOMapper;
        private final FirebaseImageService firebaseImageService;

        public UserResponse updateUser(UpdateUserRequest updateUserRequest, String id) {

            WhatEatId whatEatId = WhatEatId.builder().id(Tsid.fromString(id)).build();
            Optional<Account> accountExisting = accountRepository.findById(whatEatId);
            if (accountExisting.isPresent()) {
                if (StringUtils.isNotBlank(updateUserRequest.getEmail())) {
                    if (WhatEatRegex.checkPattern(WhatEatRegex.EMAIL_PATTERN, updateUserRequest.email))
                        accountExisting.get().setEmail(updateUserRequest.email);
                    else
                        throw WhatEatException
                                .builder()
                                .code(WhatEatErrorCode.WEV_0001)
                                .reason("email", "Invalid email address")
                                .build();
                }

                if (StringUtils.isNotBlank(updateUserRequest.getPhoneNumber())) {
                    if (WhatEatRegex.checkPattern(WhatEatRegex.PHONE_PATTERN, updateUserRequest.phoneNumber))
                        accountExisting.get().setPhoneNumber(updateUserRequest.getPhoneNumber());
                    else
                        throw WhatEatException
                                .builder()
                                .code(WhatEatErrorCode.WEV_0001)
                                .reason("email", "Invalid email address")
                                .build();
                }

                if (StringUtils.isNotBlank(updateUserRequest.getFullName())) {
                    accountExisting.get().setFullName(updateUserRequest.getFullName());
                }

                FirebaseImageResponse firebaseImageResponse = null;
                try {
                    if (StringUtils.isNotBlank(updateUserRequest.getImage())) {
                        firebaseImageResponse = firebaseImageService.uploadBase64Image(updateUserRequest.getImage());
                        accountExisting.get().setImage(firebaseImageResponse.url());
                    }
                } catch (Exception e) {
                    // Image is created. Time to delete!
                    if (firebaseImageResponse != null) {
                        firebaseImageService.deleteImage(firebaseImageResponse.id(), FirebaseImageService.DeleteType.ID);
                    }
                    if (e instanceof WhatEatException whatEatException) {
                        throw whatEatException;
                    }
                    throw WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("accouunt", "Lỗi trong việc update account")
                            .build();
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
