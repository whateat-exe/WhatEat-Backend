package com.exe.whateat.infrastructure.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class WhatEatExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({JWTVerificationException.class})
    public ResponseEntity<Object> handleJwtVerificationException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
