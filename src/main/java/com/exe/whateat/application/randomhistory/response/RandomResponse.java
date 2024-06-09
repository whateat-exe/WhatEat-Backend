package com.exe.whateat.application.randomhistory.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public final class RandomResponse {

    private int maxCount;
    private int countLeft;
    private long timeLeft;

    @JsonIgnore
    private boolean reset;

    @JsonIgnore
    public boolean notAllowedToRandomize() {
        return (timeLeft > 0);
    }

    @JsonIgnore
    public boolean shouldBeReset() {
        return reset;
    }
}
