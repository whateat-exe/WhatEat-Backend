package com.exe.whateat.application.dish.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.food.mapper.FoodMapper;
import com.exe.whateat.application.restaurant.mapper.RestaurantMapper;
import com.exe.whateat.entity.food.Dish;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DishMapper implements WhatEatMapper<Dish, DishResponse> {

    @Override
    public DishResponse convertToDto(Dish dish) {
        if (dish == null) {
            return null;
        }
        return DishResponse.builder()
                .id(dish.getId().asTsid())
                .name(dish.getName())
                .status(dish.getStatus().name())
                .image(dish.getImage())
                .price(dish.getPrice())
                .description(dish.getDescription())
                .foodId(dish.getFood().getId().asTsid())
                .restaurantId(dish.getId().asTsid())
                .build();
    }
}
