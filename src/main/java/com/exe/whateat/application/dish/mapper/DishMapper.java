package com.exe.whateat.application.dish.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.random.Rating;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class DishMapper implements WhatEatMapper<Dish, DishResponse> {

    @Override
    public DishResponse convertToDto(Dish dish) {
        if (dish == null) {
            return null;
        }

        List<Rating> ratings = dish.getRatings();
        double avgReview = 0.0;
        int numOfReview = 0;

        if (ratings != null && !ratings.isEmpty()) {
            numOfReview = ratings.size();
            double sum = ratings.stream()
                    .mapToDouble(Rating::getStars)
                    .sum();
            avgReview = Math.round(sum / numOfReview * 10) / 10.0;
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
                .avgReview(avgReview)
                .numOfReview((double) numOfReview)
                .build();
    }
}
