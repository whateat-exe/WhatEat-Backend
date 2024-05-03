package com.exe.whateat.application.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WhatEatErrorCode {

    WEB_0000("Dummy business error code"),
    WEV_0000("Dummy validation error code"),
    WES_0000("Dummy server error code");

    private final String reason;

    @Override
    public String toString() {
        return name().replace("_", "-");
    }
}
