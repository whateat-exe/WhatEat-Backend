package com.exe.whateat.infrastructure.converter.hibernate;

import io.github.x4ala1c.tsid.Tsid;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WhatEatTsidConverter implements AttributeConverter<Tsid, Long> {

    @Override
    public Long convertToDatabaseColumn(Tsid attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.asLong();
    }

    @Override
    public Tsid convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            return null;
        }
        return Tsid.fromLong(dbData);
    }
}
