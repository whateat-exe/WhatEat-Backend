package com.exe.whateat.application.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@SuppressWarnings("unused")
public final class WhatEatErrorResponse {

    public record Error(String target, String message) {

        public Error {
            if (StringUtils.isBlank(target) || StringUtils.isBlank(message)) {
                throw new IllegalArgumentException("Invalid target or error message");
            }
        }
    }

    private final String code;
    private final String title;
    private final List<Error> reasons;
    private final Instant createdAt;

    public WhatEatErrorResponse(String code, String title, List<Error> reasons) {
        this.code = code;
        this.title = title;
        this.reasons = reasons;
        this.createdAt = Instant.now();
    }

    public static final class Builder {

        private WhatEatErrorCode code;
        private List<Error> reasons;

        private Builder() {
            this.reasons = new LinkedList<>();
        }

        public Builder code(WhatEatErrorCode code) {
            this.code = code;
            return this;
        }

        public Builder reason(Error reason) {
            if (this.reasons == null) {
                throw new NullPointerException("Reasons cannot be null");
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
            this.reasons = Objects.requireNonNullElseGet(reasons, LinkedList::new);
            return this;
        }

        public WhatEatErrorResponse build() {
            return new WhatEatErrorResponse(code.toString(), code.getTitle(), reasons);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
