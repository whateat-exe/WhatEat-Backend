package com.exe.whateat.infrastructure.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenerationCode {

    private static final Random RANDOM = new SecureRandom();

    public static String codeGeneration() {
        int number = RANDOM.nextInt(999999);
        return String.format("%06d", number);
    }
}
