package com.exe.whateat.infrastructure.service.account.verification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class VerificationCodeGenerator {

    private static final Random RANDOM = new SecureRandom();
    private static final int MAX_CODE_VALUE = 1000000;

    static String generate() {
        int number = RANDOM.nextInt(MAX_CODE_VALUE);
        return String.format("%06d", number);
    }
}
