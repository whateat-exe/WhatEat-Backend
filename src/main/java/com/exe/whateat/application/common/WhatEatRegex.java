package com.exe.whateat.application.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhatEatRegex {

    public static final String phonePattern = "^0\\d{9}$";
    public static final String emailPattern = "^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public static final boolean checkPattern(String strPattern, String input) {
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
