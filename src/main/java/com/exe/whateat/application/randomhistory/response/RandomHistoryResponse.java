package com.exe.whateat.application.randomhistory.response;

import com.exe.whateat.application.food.response.FoodResponse;
import lombok.Builder;

import java.time.Instant;

@Builder
public record RandomHistoryResponse(FoodResponse food, Instant createdAt) {

}
