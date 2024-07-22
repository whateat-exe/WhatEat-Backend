package com.exe.whateat.application.restaurant_response.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.restaurant_response.response.RestaurantResponseResponse;
import com.exe.whateat.entity.request.RestaurantRequestResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RestaurantResponseMapper implements WhatEatMapper<RestaurantRequestResponse, RestaurantResponseResponse> {

    @Override
    public RestaurantResponseResponse convertToDto(RestaurantRequestResponse restaurantRequestResponse) {
        if (restaurantRequestResponse == null) {
            return null;
        }
        final RestaurantResponseResponse.RestaurantResponseResponseBuilder builder = RestaurantResponseResponse.builder()
                .id(restaurantRequestResponse.getId().asTsid())
                .content(restaurantRequestResponse.getContent())
                .title(restaurantRequestResponse.getTitle())
                .createdAt(Instant.now())
                .restaurantRequestId(restaurantRequestResponse.getRestaurantRequest().getId().asTsid())
                .status(restaurantRequestResponse.getStatus());
        return builder.build();
    }
}
