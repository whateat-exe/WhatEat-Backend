package com.exe.whateat.application.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public final class WhatEatErrorResponse {

    private final String code;
    private final String title;
    private final String message;
    private final LocalDateTime createdAt;

    @Builder
    public WhatEatErrorResponse(String code, String title, String message) {
        this.code = code;
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}
