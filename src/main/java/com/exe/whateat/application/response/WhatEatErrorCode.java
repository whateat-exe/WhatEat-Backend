package com.exe.whateat.application.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    WEV_0000("Dummy validation error code"),
    WES_0000("Dummy server error code"),
    WEA_0000("Access denied"),
    WEA_0001("Not authorized");

    private final String title;

    @Override
    public String toString() {
        return name().replace("_", "-");
    }
}
