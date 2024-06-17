package com.exe.whateat.infrastructure.converter.spring;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import io.github.x4ala1c.tsid.Tsid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public final class TsidConverter implements Converter<String, Tsid> {

    @Override
    public Tsid convert(@NonNull String source) {
        try {
            if (StringUtils.isBlank(source)) {
                return null;
            }
            return Tsid.fromString(source);
        } catch (Exception e) {
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEV_0005)
                    .reason("id", "Must be Crockford's Base32 representation of TSID.")
                    .build();
        }
    }
}
