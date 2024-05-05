package com.exe.whateat.application.exception;

@SuppressWarnings("unused")
public final class WhatEatValidationException extends RuntimeException {

    public WhatEatValidationException(String message) {
        super(message);
    }

    public WhatEatValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhatEatValidationException(Throwable cause) {
        super(cause);
    }
}
