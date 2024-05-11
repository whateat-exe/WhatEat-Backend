package com.exe.whateat.application.account_user.createMethod;

import com.exe.whateat.application.account_user.createMethod.request.CreateUserRequest;
import com.exe.whateat.application.account_user.createMethod.response.CreateUserResponse;
import com.exe.whateat.application.account_user.dto.UserDTO;
import com.exe.whateat.application.account_user.mapper.AccountDTOMapper;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatRegex;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

public final class CreateUser {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for create a user"
    )
    public static final class CreateUserController extends AbstractController {

        private final CreateUserService createUserService;
        private final WhatEatSecurityHelper whatEatSecurityHelper;

        @PostMapping("/users")
        @ApiResponse(
                responseCode = "201",
                description = "Create a new user successfully",
                content = @Content(schema = @Schema(implementation = CreateUserResponse.class))
        )
        @ApiResponse(
                responseCode = "400s",
                description = "Can not create a new user",
                content = @Content(schema = @Schema(implementation = CreateUserResponse.class))
        )
        public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
            Optional<Account> account = whatEatSecurityHelper.getCurrentLoggedInAccount();
            final CreateUserResponse userResponse = createUserService.createUserService(createUserRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class CreateUserService {

        private final AccountRepository accountRepository;
        private final PasswordEncoder passwordEncoder;
        private final AccountDTOMapper accountDTOMapper;

        public CreateUserResponse createUserService(CreateUserRequest createUserRequest) {
            var email = createUserRequest.getEmail();
            boolean checkEmail = WhatEatRegex.checkPattern(WhatEatRegex.emailPattern, createUserRequest.getEmail());
            if (!checkEmail)
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEV_0001)
                        .reason("email", "Wrong email pattern")
                        .build();

            var phoneNumberCheck = WhatEatRegex.checkPattern(WhatEatRegex.phonePattern, createUserRequest.getPhoneNumber());
            if (!phoneNumberCheck)
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEV_0008)
                        .reason("phone number", "Wrong phone number pattern")
                        .build();

            Optional<Account> account = accountRepository.findByEmail(email);
            if (!account.isEmpty()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEV_0001)
                        .reason("email", "Email has been registered")
                        .build();
            }
            else {
                String passwordEncode = passwordEncoder.encode(createUserRequest.getPassword());
                WhatEatId whatEatId = WhatEatId.generate();
                Account accountCreate =
                        Account.builder()
                                .id(whatEatId)
                                .email(createUserRequest.getEmail())
                                .status(ActiveStatus.INACTIVE)
                                .fullName(createUserRequest.getFullName())
                                .password(passwordEncode)
                                .role(AccountRole.USER)
                                .phoneNumber(createUserRequest.getPhoneNumber())
                                .build();
                Account accountCreated = accountRepository.save(accountCreate);
                UserDTO userDTO = accountDTOMapper.apply(accountCreated);
                CreateUserResponse userResponse =
                        CreateUserResponse.builder()
                                .userDTO(userDTO)
                                .build();
                return userResponse;
            }
        }
    }}
