package com.exe.whateat.infrastructure.format;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstantConverter {

    public static String convertInstantFormat(Instant instant) {
        if (instant == null)
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("created at", "Thời gian tạo ra không có")
                    .build();
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zdtToConvert = instant.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String formattedTime = zdtToConvert.format(formatter);
        return formattedTime;
    }
}
