package com.exe.whateat.application.foodtag.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
import com.exe.whateat.application.tag.response.TagResponse;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.Tag;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class FoodTagMapper implements WhatEatMapper<FoodTag, FoodTagResponse> {

    private final WhatEatMapper<Tag, TagResponse> tagMapper;
    private final WhatEatMapper<Food, FoodResponse> foodMapper;

    @Override
    public FoodTagResponse convertToDto(FoodTag foodTag) {
        if (foodTag == null) {
            return null;
        }
        return FoodTagResponse.builder()
                .id(foodTag.getId().asTsid())
                .tag(tagMapper.convertToDto(foodTag.getTag()))
                .food(foodMapper.convertToDto(foodTag.getFood()))
                .build();
    }
}
