package com.exe.whateat.infrastructure.exception;

import com.exe.whateat.application.exception.WhatEatErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public final class WhatEatErrorResponse {

    public record Error(String title, String message) {

        public Error {
            if (StringUtils.isBlank(title) || StringUtils.isBlank(message)) {
                throw new IllegalArgumentException("Invalid title or error message");
            }
        }
    }

    private final String code;
    private final String title;
    private final List<Error> reasons;
    private final Instant createdAt;

    private WhatEatErrorResponse(Builder builder) {
        this.code = builder.code.toString();
        this.title = builder.code.getTitle();
        this.reasons = builder.reasons;
        this.createdAt = Instant.now();
    }

    public static final class Builder {

        private WhatEatErrorCode code;
        private final List<Error> reasons;

        private Builder() {
            this.reasons = new LinkedList<>();
        }

        public Builder code(WhatEatErrorCode code) {
            this.code = code;
            return this;
        }

        public Builder reason(Error reason) {
            if (reason == null) {
                throw new NullPointerException("Reason cannot be null");
            }
            this.reasons.add(reason);
            return this;
        }

        public Builder reason(String target, String message) {
            final Error reason = new Error(target, message);
            this.reasons.add(reason);
            return this;
        }

        public Builder reasons(List<Error> reasons) {
            if (reasons == null) {
                throw new NullPointerException("Reasons cannot be null");
            }
            this.reasons.addAll(reasons);
            return this;
        }

        public WhatEatErrorResponse build() {
            return new WhatEatErrorResponse(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
