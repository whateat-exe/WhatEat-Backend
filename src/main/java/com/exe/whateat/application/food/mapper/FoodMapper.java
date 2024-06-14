package com.exe.whateat.application.food.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.entity.food.Food;
import org.springframework.stereotype.Component;

@Component
public final class FoodMapper implements WhatEatMapper<Food, FoodResponse> {

    @Override
    public FoodResponse convertToDto(Food food) {
        if (food == null) {
            return null;
        }
        final FoodResponse.FoodResponseBuilder builder = FoodResponse.builder()
                .id(food.getId().asTsid())
                .name(food.getName())
                .status(food.getStatus().name())
                .image(food.getImage());
        return builder.build();
    }
}
