package com.exe.whateat.infrastructure.jackson;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.Instant;

@JsonComponent
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WhatEatInstantHandler {

    public static final class InstantSerializer extends JsonSerializer<Instant> {

        @Override
        public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (instant == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeString(Long.toString(instant.toEpochMilli()));
            }
        }
    }

    public static final class InstantDeserializer extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            try {
                final String value = jsonParser.getValueAsString();
                return Instant.ofEpochMilli(Long.parseLong(value));
            } catch (JacksonException | NumberFormatException e) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0004)
                        .reason("timestamp", "Invalid timestamp format.")
                        .build();
            }
        }
    }
}
