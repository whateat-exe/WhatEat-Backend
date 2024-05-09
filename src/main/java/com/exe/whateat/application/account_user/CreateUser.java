//package com.exe.whateat.application.account_user;
//
//import com.exe.whateat.application.exception.WhatEatErrorCode;
//import com.exe.whateat.application.exception.WhatEatException;
//import com.exe.whateat.entity.account.Account;
//import com.exe.whateat.infrastructure.repository.AccountRepository;
//import lombok.AllArgsConstructor;
//import lombok.SneakyThrows;
//import org.apache.commons.validator.routines.EmailValidator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//public final class CreateUser {
//
//    private static final int PASSWORD_MIN_LENGTH = 8;
//    private static final int PASSWORD_MAX_LENGTH = 32;
//    private static final int FULLNAME_MAX_LENGTH = 100;
//    private static final int PHONE_NUMBER_MAX_LENGTH = 20;
//    private static final String INVALID_PASSWORD_LENGTH_MESSAGE =
//            String.format("The password length has to be in %d and %d", PASSWORD_MIN_LENGTH, PASSWORD_MAX_LENGTH);
//    private static final String INVALID_FULLNAME_LENGTH_MESSAGE =
//            String.format("The full name of account has to be in %d and %d", 0, FULLNAME_MAX_LENGTH);
//    private static final String INVALID_PHONE_NUMBER_LENGTH_MESSAGE =
//            String.format("The phone number has to be in %d and %d", 0, PHONE_NUMBER_MAX_LENGTH);
//
//    public record CreateUserRequest(String email, String password, String phoneNumber, String fullName){
//        @SneakyThrows
//        public CreateUserRequest(String email, String password, String phoneNumber, String fullName) {
//            final String emailTrim = email.trim();
//            if (emailTrim.isBlank()) {
//                throw  WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0001)
//                        .reason("email", "Email is required")
//                        .build();
//            }
//            if (!EmailValidator.getInstance().isValid(emailTrim) {
//                throw WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0001)
//                        .reason("email", "Email has invalid format")
//                        .build();
//            }
//
//            final String passwordTrim = password.trim();
//            if (passwordTrim.isBlank()) {
//                throw WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0002)
//                        .reason("password", "password is required")
//                        .build();
//            }
//            if(passwordTrim.length() < 0 || passwordTrim.length() > PASSWORD_MAX_LENGTH) {
//                throw WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0002)
//                        .reason("password", INVALID_PASSWORD_LENGTH_MESSAGE)
//                        .build();
//            }
//            final String fullNameTrim = fullName.trim();
//            if(fullNameTrim.isBlank()) {
//                throw WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0006)
//                        .reason("FullName", "Full Name is required")
//                        .build();
//            }
//            if (fullNameTrim.length() < 0 || fullNameTrim.length() > FULLNAME_MAX_LENGTH) {
//                throw WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0006)
//                        .reason("FullName", INVALID_FULLNAME_LENGTH_MESSAGE)
//                        .build();
//            }
//            final String phoneNumberTrim = phoneNumber.trim();
//            if(phoneNumberTrim.isBlank()) {
//                throw WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0007)
//                        .reason("PhoneNumber", "Phone Number is required")
//                        .build();
//            }
//            if (fullNameTrim.length() < 0 || fullNameTrim.length() > FULLNAME_MAX_LENGTH) {
//                throw WhatEatException.builder()
//                        .code(WhatEatErrorCode.WEV_0007)
//                        .reason("PhoneNumber", INVALID_PHONE_NUMBER_LENGTH_MESSAGE)
//                        .build();
//            }
//            this.email = emailTrim;
//            this.password = passwordTrim;
//            this.fullName = fullNameTrim;
//            this.phoneNumber = phoneNumberTrim;
//        }
//    }
//
//    public record CreateUserResponse(Account user) {
//
//    }
//
//    @RestController
//    @AllArgsConstructor
//    public final class CreateUserController {
//
//        private final CreateUserService createUserService;
//    }
//
//    @Servicce
//    @AllArgsConstructor
//    public final class CreateUserService {
//
//        private final AccountRepository accountRepository;
//
//        public createUserService(CreateUserRequest createUserRequest) {
//
//        }
//    }}
