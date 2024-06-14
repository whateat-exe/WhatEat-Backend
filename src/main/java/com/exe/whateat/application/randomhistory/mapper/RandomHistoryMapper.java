package com.exe.whateat.application.randomhistory.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.randomhistory.response.RandomHistoryResponse;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.random.RandomHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public final class RandomHistoryMapper implements WhatEatMapper<RandomHistory, RandomHistoryResponse> {

    @Value("${whateat.tsid.epoch}")
    private long epoch;

    private final WhatEatMapper<Food, FoodResponse> foodMapper;

    @Autowired
    public RandomHistoryMapper(WhatEatMapper<Food, FoodResponse> foodMapper) {
        this.foodMapper = foodMapper;
    }

    @Override
    public RandomHistoryResponse convertToDto(RandomHistory randomHistory) {
        if (randomHistory == null) {
            return null;
        }
        return RandomHistoryResponse.builder()
                .food(foodMapper.convertToDto(randomHistory.getFood()))
                .createdAt(Instant.ofEpochMilli(epoch + (randomHistory.getId().asTsid().asLong() >>> 22)))
                .build();
    }
}
