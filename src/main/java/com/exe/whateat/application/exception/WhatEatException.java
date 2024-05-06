package com.exe.whateat.application.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public final class WhatEatException extends RuntimeException {

    public record Reason(String title, String reason) {

        public Reason(String title, String reason) {
            if (StringUtils.isBlank(title)) {
                throw new IllegalArgumentException("Title is blank");
            }
            if (StringUtils.isBlank(reason)) {
                throw new IllegalArgumentException("Reason is blank");
            }
            this.title = title.trim();
            this.reason = reason.trim();
        }
    }

    @Getter
    private final WhatEatErrorCode code;

    private final transient List<Reason> reasons;

    private WhatEatException(Builder builder) {
        this.code = builder.code;
        this.reasons = builder.reasons;
    }

    public static final class Builder {

        private WhatEatErrorCode code;
        private final List<Reason> reasons;

        private Builder() {
            this.reasons = new LinkedList<>();
        }

        public Builder code(WhatEatErrorCode code) {
            if (code == null) {
                throw new NullPointerException("Code is null");
            }
            this.code = code;
            return this;
        }

        public Builder reason(Reason reason) {
            if (reason == null) {
                throw new NullPointerException("Reason is null");
            }
            this.reasons.add(reason);
            return this;
        }

        public Builder reasons(Collection<Reason> reasons) {
            if (reasons == null) {
                throw new NullPointerException("Reasons is null");
            }
            this.reasons.addAll(reasons);
            return this;
        }

        public Builder reason(String title, String reason) {
            final Reason reasonObj = new Reason(title, reason);
            this.reasons.add(reasonObj);
            return this;
        }

        public WhatEatException build() {
            return new WhatEatException(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Reason> getReasons() {
        return reasons.stream().toList();
    }

    public void addReason(Reason reason) {
        if (reason == null) {
            throw new NullPointerException("Reason is null");
        }
        this.reasons.add(reason);
    }

    public void addReasons(Collection<Reason> reasons) {
        if (reasons == null) {
            throw new NullPointerException("Reasons is null");
        }
        this.reasons.addAll(reasons);
    }

    public void addReason(String title, String reason) {
        final Reason reasonObj = new Reason(title, reason);
        this.reasons.add(reasonObj);
    }
}
