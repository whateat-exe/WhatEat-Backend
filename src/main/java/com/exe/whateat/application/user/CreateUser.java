package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatRegex;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.email.SendEmailService;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateUser {

    @Getter
    @Setter
    public static class CreateUserRequest {

        @NotBlank(message = "Email is required")
        @Email
        private String email;

        @NotNull
        @Size(min = 8, max = 32, message = "The length of password has to be larger than 8 and less than 32 ")
        private String password;

        @NotBlank
        @Size(max = 100, message = "The length of full name has to be less than 100")
        private String fullName;

        @NotBlank
        @Size(max = 20, message = "The length of phone number has to be less than 20")
        private String phoneNumber;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for create a user"
    )
    public static final class CreateUserController extends AbstractController {

        private final CreateUserService createUserService;

        @PostMapping("/users")
        @ApiResponse(
                responseCode = "201",
                description = "Create a new user successfully",
                content = @Content(schema = @Schema(implementation = UserResponse.class))
        )
        @ApiResponse(
                responseCode = "400s",
                description = "Can not create a new user",
                content = @Content(schema = @Schema(implementation = UserResponse.class))
        )
        public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
            final UserResponse userResponse = createUserService.createUserService(createUserRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class CreateUserService {

        private final AccountRepository accountRepository;
        private final PasswordEncoder passwordEncoder;
        private final AccountDTOMapper accountDTOMapper;
        private SendEmailService sendEmailService;

        public UserResponse createUserService(CreateUserRequest createUserRequest) {
            var email = createUserRequest.getEmail();
            var phoneNumberCheck = WhatEatRegex.checkPattern(WhatEatRegex.phonePattern, createUserRequest.getPhoneNumber());
            if (!phoneNumberCheck)
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEV_0008)
                        .reason("phone number", "Wrong phone number pattern")
                        .build();

            Optional<Account> account = accountRepository.findByEmail(email);
            if (account.isPresent()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEV_0001)
                        .reason("email", "Email has been registered")
                        .build();
            } else {
                String passwordEncode = passwordEncoder.encode(createUserRequest.getPassword());
                WhatEatId whatEatId = WhatEatId.generate();
                Account accountCreate =
                        Account.builder()
                                .id(whatEatId)
                                .email(createUserRequest.getEmail())
                                .status(ActiveStatus.ACTIVE)
                                .fullName(createUserRequest.getFullName())
                                .password(passwordEncode)
                                .role(AccountRole.USER)
                                .phoneNumber(createUserRequest.getPhoneNumber())
                                .build();
                Account accountCreated = accountRepository.save(accountCreate);
                if (accountCreated != null) {
                    sendEmailService.sendMail(accountCreated.getEmail(), "Test body", "Test subject");
                }
                return accountDTOMapper.convertToDto(accountCreated);
            }
        }
    }
}
