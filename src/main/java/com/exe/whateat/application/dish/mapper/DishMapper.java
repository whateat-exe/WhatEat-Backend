package com.exe.whateat.application.dish.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DishMapper implements WhatEatMapper<Dish, DishResponse> {

    private final DishRepository dishRepository;
    private final WhatEatMapper<Food, FoodResponse> foodMapper;
    private final WhatEatMapper<Restaurant, RestaurantResponse> restaurantMapper;

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
                .food(foodMapper.convertToDto(dish.getFood()))
                .restaurant(restaurantMapper.convertToDto(dish.getRestaurant()))
                .avgReview(formatAvg(dishRepository.findAverageRatingByDishId(dish.getId())))
                .numOfReview(dishRepository.countRatingsByDishId(dish.getId()))
                .build();
    }

    private Double formatAvg(Double avg) {
        return avg != null ? Math.round(avg * 10) / 10.0 : null;
    }
}
