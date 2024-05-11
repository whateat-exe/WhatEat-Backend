package com.exe.whateat.application.restaurant.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.entity.restaurant.Restaurant;
import org.springframework.stereotype.Component;

@Component
public final class RestaurantMapper implements WhatEatMapper<Restaurant, RestaurantResponse> {

    @Override
    public RestaurantResponse convertToDto(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }
        return RestaurantResponse.builder()
                .id(restaurant.getId().asTsid())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .description(restaurant.getDescription())
                .image(restaurant.getImage())
                .status(restaurant.getStatus())
                .build();
    }
}
