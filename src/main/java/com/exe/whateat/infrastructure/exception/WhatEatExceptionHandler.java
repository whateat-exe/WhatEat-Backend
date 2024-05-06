package com.exe.whateat.infrastructure.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WhatEatExceptionHandler {

    @ExceptionHandler({JWTVerificationException.class})
    public ResponseEntity<WhatEatErrorResponse> handleJwtVerificationException() {
        final WhatEatException whatEatException = WhatEatException.builder()
                .code(WhatEatErrorCode.WEA_0003)
                .reason("auth_token", "Invalid authentication token.")
                .build();
        return createResponse(whatEatException);
    }

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

    @ExceptionHandler
    public ResponseEntity<WhatEatErrorResponse> handleWhatEatException(WhatEatException ex) {
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
            return ResponseEntity.status(whateatException.getCode().asHttpStatus())
                    .body(response);
        }
        // Unknown exception. Return as Internal Server Error.
        final WhatEatErrorResponse response = WhatEatErrorResponse.builder()
                .code(WhatEatErrorCode.WES_0001)
                .reason("Unknown", "Oopsie! Something's wrong with our server. Do try again!")
                .build();
        return ResponseEntity.internalServerError().body(response);
    }
}
