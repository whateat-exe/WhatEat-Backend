package com.exe.whateat.application.dish.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.food.mapper.FoodMapper;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.restaurant.mapper.RestaurantMapper;
import com.exe.whateat.entity.food.Dish;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DishMapper extends WhatEatMapper<Dish, DishResponse> {

    private FoodMapper foodMapper;
    private RestaurantMapper restaurantMapper;

    @Override
    public DishResponse convertToDto(Dish dish) {
        if (dish == null) {
            return null;
        }
        return  DishResponse.builder()
                .id(dish.getId().asTsid())
                .name(dish.getName())
                .status(dish.getStatus().name())
                .image(dish.getImage())
                .build();
    }
}
