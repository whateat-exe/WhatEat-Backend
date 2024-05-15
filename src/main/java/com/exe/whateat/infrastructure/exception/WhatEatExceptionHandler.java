package com.exe.whateat.infrastructure.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.google.cloud.storage.StorageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class WhatEatExceptionHandler {

    @ExceptionHandler({AuthenticationException.class, UsernameNotFoundException.class})
    public ResponseEntity<WhatEatErrorResponse> handleAuthenticationException(Exception ex) {
        if (ex instanceof DisabledException || ex instanceof LockedException) {
            final WhatEatException whatEatException = WhatEatException.builder()
                    .code(WhatEatErrorCode.WEA_0004)
                    .reason("account", "Account is inactive. Contact admin for further guidance.")
                    .build();
            return createResponse(whatEatException);
        }
        if (ex instanceof BadCredentialsException || ex instanceof UsernameNotFoundException) {
            final WhatEatException whatEatException = WhatEatException.builder()
                    .code(WhatEatErrorCode.WEA_0005)
                    .reason("credential", "Invalid email or password.")
                    .build();
            return createResponse(whatEatException);
        }
        // Unknown exception. Resort to server error.
        return createResponse(ex);
    }

    @ExceptionHandler({StorageException.class})
    public ResponseEntity<WhatEatErrorResponse> handleFirebaseException() {
        final WhatEatException whatEatException = WhatEatException.builder()
                .code(WhatEatErrorCode.WES_0003)
                .reason("firebase", "Unable to communicate with Firebase.")
                .build();
        return createResponse(whatEatException);
    }

    // Jakarta Validation handling mechanism

    @ExceptionHandler
    public ResponseEntity<WhatEatErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        final List<ObjectError> errors = ex.getAllErrors();
        final List<WhatEatErrorResponse.Error> mappedErrors = errors.stream()
                .map(e -> {
                    if (e instanceof FieldError fieldError) {
                        return new WhatEatErrorResponse.Error(fieldError.getField(), fieldError.getDefaultMessage());
                    }
                    return new WhatEatErrorResponse.Error(e.getObjectName(), e.getDefaultMessage());
                })
                .toList();
        return ResponseEntity.badRequest().body(WhatEatErrorResponse.builder()
                .code(WhatEatErrorCode.WEV_0000)
                .reasons(mappedErrors)
                .build());
    }

    // JWT Verification handling mechanism

    @ExceptionHandler({JWTVerificationException.class})
    public ResponseEntity<WhatEatErrorResponse> handleJwtException() {
        final WhatEatException whatEatException = WhatEatException.builder()
                .code(WhatEatErrorCode.WEA_0003)
                .reason("token", "Token xác thực không hợp lệ.")
                .build();
        return createResponse(whatEatException);
    }

    // WhatEatException handling mechanism

    @ExceptionHandler
    public ResponseEntity<WhatEatErrorResponse> handleWhatEatException(Exception ex) {
        return createResponse(ex);
    }

    private ResponseEntity<WhatEatErrorResponse> createResponse(Exception ex) {
        if (ex instanceof WhatEatException whateatException) {
            final WhatEatErrorResponse response = WhatEatErrorResponse.builder()
                    .code(whateatException.getCode())
                    .reasons(whateatException.getReasons().stream()
                            .map(r -> new WhatEatErrorResponse.Error(r.title(), r.reason()))
                            .toList())
                    .build();
            return ResponseEntity.status(whateatException.getCode().getStatus())
                    .body(response);
        }
        // Unknown exception. Return as Internal Server Error.
        final WhatEatErrorResponse response = WhatEatErrorResponse.builder()
                .code(WhatEatErrorCode.WES_0001)
                .reason("Unknown", "Oopsie! Something's wrong with our server. Try again!")
                .build();
        return ResponseEntity.internalServerError().body(response);
    }
}
