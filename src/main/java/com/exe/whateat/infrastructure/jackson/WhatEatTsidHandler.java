package com.exe.whateat.infrastructure.jackson;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.x4ala1c.tsid.Tsid;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WhatEatTsidHandler {

    public static final class TsidSerializer extends JsonSerializer<Tsid> {

        @Override
        public void serialize(Tsid tsid, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (tsid == null) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeString(tsid.asString());
            }
        }
    }

    public static final class TsidDeserializer extends JsonDeserializer<Tsid> {

        @Override
        public Tsid deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
            try {
                final String value = jsonParser.getValueAsString();
                return Tsid.fromString(value);
            } catch (Exception e) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0005)
                        .reason("id", "Must be Crockford's Base32 representation of TSID.")
                        .build();
            }
        }
    }
}
