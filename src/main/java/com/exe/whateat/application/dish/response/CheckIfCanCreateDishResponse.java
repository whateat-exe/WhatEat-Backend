package com.exe.whateat.application.dish.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class CheckIfCanCreateDishResponse {

    private String message;
    private Boolean canCreateDish;

}
