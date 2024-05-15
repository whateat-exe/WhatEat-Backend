package com.exe.whateat.application.food.response;

import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;

@Builder
public record FoodResponse(Tsid id, String name, String image, Tsid parentFood, String status) {

}
