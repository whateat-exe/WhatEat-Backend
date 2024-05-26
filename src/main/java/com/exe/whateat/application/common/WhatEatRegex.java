package com.exe.whateat.application.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WhatEatRegex {

    public static final String PHONE_PATTERN = "^0\\d{9}$";
    public static final String EMAIL_PATTERN = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public static boolean checkPattern(String strPattern, String input) {
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
