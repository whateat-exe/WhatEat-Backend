package com.exe.whateat.application.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhatEatRegex {

    public static final String phonePattern = "^0\\d{9}$";
    public static final String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$";

    public static final boolean checkPattern(String strPattern, String input) {
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
