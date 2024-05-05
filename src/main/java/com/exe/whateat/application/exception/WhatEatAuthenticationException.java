package com.exe.whateat.application.exception;

@SuppressWarnings("unused")
public final class WhatEatAuthenticationException extends RuntimeException {

    public WhatEatAuthenticationException(String message) {
        super(message);
    }

    public WhatEatAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhatEatAuthenticationException(Throwable cause) {
        super(cause);
    }
}
