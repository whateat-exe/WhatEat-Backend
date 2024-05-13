package com.exe.whateat.application.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Guidelines:
 * <ul>
 * <li> WEB: Business errors.
 * <li> WEV: Validation errors.
 * <li> WES: Server errors.
 * <li> WEA: Authentication/Authorization errors.
 * </ul>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WhatEatErrorCode {

    WEB_0000("Dummy business error code"),
    WEB_0001("Restaurant name or email already exists"),
    WEB_0002("Restaurant not found"),
    WEB_0003("Invalid restaurant status"),
    WEV_0000("Invalid request"),
    WEV_0001("Invalid email address"),
    WEV_0002("Invalid password"),
    WEV_0003("Invalid amount"),
    WEV_0004("Invalid timestamp format"),
    WEV_0005("Invalid ID format"),
    WEV_0006("Invalid image format"),
    WEV_0007("Invalid phone number"),
    WEV_0008("Invalid full name"),
    WEV_0009("Oversize image"),
    WES_0000("Dummy server error"),
    WES_0001("Unknown error"),
    WES_0002("Unknown user account"),
    WES_0003("3rd party service error"),
    WES_0004("Unknown image URL"),
    WEA_0000("Dummy authorization/authentication error code"),
    WEA_0001("Not authorized"),
    WEA_0002("Forbidden"),
    WEA_0003("Invalid authentication token"),
    WEA_0004("Inactive account"),
    WEA_0005("Invalid login credentials"),
    WEA_0006("Invalid refresh token"),
    WEA_0007("Internal Server");

    private static final String BUSINESS_CODE = "WEB";
    private static final String VALIDATION_CODE = "WEV";
    private static final String SERVER_ERROR_CODE = "WES";
    private static final String AUTH_CODE = "WEA";

    private final String title;

    @Override
    public String toString() {
        return name().replace("_", "-");
    }

    public HttpStatus asHttpStatus() {
        if (this == WEA_0001) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (this.name().startsWith(BUSINESS_CODE) || this.name().startsWith(VALIDATION_CODE)) {
            return HttpStatus.BAD_REQUEST;
        }
        if (this.name().startsWith(AUTH_CODE)) {
            return HttpStatus.FORBIDDEN;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
