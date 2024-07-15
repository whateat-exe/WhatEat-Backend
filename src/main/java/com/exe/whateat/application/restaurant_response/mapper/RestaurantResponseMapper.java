package com.exe.whateat.application.restaurant_response.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.restaurant_response.response.RestaurantResponseResponse;
import com.exe.whateat.entity.request.RestaurantRequestResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@AllArgsConstructor
@Component
@Builder
public class RestaurantResponseMapper implements WhatEatMapper<RestaurantRequestResponse, RestaurantResponseResponse> {

    @Override
    public RestaurantResponseResponse convertToDto(RestaurantRequestResponse restaurantRequestResponse) {
        if (restaurantRequestResponse == null) {
            return null;
        }
        final RestaurantResponseResponse.RestaurantResponseResponseBuilder builder = RestaurantResponseResponse.builder()
                .tsid(restaurantRequestResponse.getId().asTsid())
                .content(restaurantRequestResponse.getContent())
                .title(restaurantRequestResponse.getTitle())
                .createdAt(Instant.now())
                .restaurantRequestId(restaurantRequestResponse.getRestaurantRequest().getId().asTsid())
                .status(restaurantRequestResponse.getStatus());
        return builder.build();
    }
}
