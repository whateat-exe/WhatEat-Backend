package com.exe.whateat.application.dish.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.infrastructure.repository.DishRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class DishMapper implements WhatEatMapper<Dish, DishResponse> {

    DishRepository dishRepository;

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
                .restaurantId(dish.getRestaurant().getId().asTsid())
                .avgReview(formatAvg(dishRepository.findAverageRatingByDishId(dish.getId())))
                .numOfReview(dishRepository.countRatingsByDishId(dish.getId()))
                .build();
    }

    private double formatAvg(Double avg) {
        return Math.round(avg * 10) / 10.0;
    }
}
