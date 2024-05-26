package com.exe.whateat.application.user;

import com.exe.whateat.application.account.verification.AccountVerificationService;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatRegex;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateUser {

    @Getter
    @Setter
    public static class CreateUserRequest {

        @NotBlank(message = "Email là bắt buộc.")
        @Email(message = "Email phải có cấu trúc hợp lệ của một email.")
        private String email;

        @NotBlank(message = "Mật khẩu là bắt buộc.")
        @Size(min = 8, max = 32, message = "Mật khẩu phải từ 8 đến 32 kí tự.")
        private String password;

        @NotBlank(message = "Tên đầy đủ là bắt buộc.")
        @Size(min = 1, max = 100, message = "Tên đầy đủ phải dưới hoặc bằng 100 kí tự.")
        private String fullName;

        @NotBlank(message = "Số điện thoại là bắt buộc.")
        @Size(min = 1, max = 20, message = "Số điện thoại phải dưới 20 chữ số.")
        private String phoneNumber;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for user accounts."
    )
    public static final class CreateUserController extends AbstractController {

        private final CreateUserService createUserService;

        @PostMapping("/users")
        @Operation(
                summary = "Create/Register user account.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the user.",
                        content = @Content(schema = @Schema(implementation = CreateUserRequest.class))
                )
        )
        @ApiResponse(
                responseCode = "204",
                description = "Create a new user successfully. No content will be returned."
        )
        @ApiResponse(
                responseCode = "400s/500s",
                description = "Can not create a new user",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
            createUserService.createUserService(createUserRequest);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class CreateUserService {

        private final AccountRepository accountRepository;
        private final PasswordEncoder passwordEncoder;
        private final AccountVerificationService accountVerificationService;

        public void createUserService(CreateUserRequest createUserRequest) {
            var email = createUserRequest.getEmail();
            var phoneNumberCheck = WhatEatRegex.checkPattern(WhatEatRegex.PHONE_PATTERN, createUserRequest.getPhoneNumber());
            if (!phoneNumberCheck)
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEV_0008)
                        .reason("phoneNumber", "Số điện thoại không đúng format quy định.")
                        .build();
            if (accountRepository.existsByEmail(email)) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEV_0001)
                        .reason("email", "Email này đã được đăng ký sử dụng trước đó.")
                        .build();
            }
            final String passwordEncode = passwordEncoder.encode(createUserRequest.getPassword());
            final WhatEatId whatEatId = WhatEatId.generate();
            Account createdAccount = Account.builder()
                    .id(whatEatId)
                    .email(email)
                    .status(ActiveStatus.PENDING)
                    .fullName(createUserRequest.getFullName())
                    .password(passwordEncode)
                    .role(AccountRole.USER)
                    .phoneNumber(createUserRequest.getPhoneNumber())
                    .build();
            createdAccount = accountRepository.save(createdAccount);
            accountVerificationService.sendVerificationCode(createdAccount);
        }
    }
}
