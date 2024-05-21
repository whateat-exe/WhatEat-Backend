package com.exe.whateat.infrastructure.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import java.util.Random;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class GenerationCode {

    private static Random random = new Random();

    public static String codeGeneration() {
        int number = random.nextInt(999999);

        return String.format("%06d", number);
    }
}
