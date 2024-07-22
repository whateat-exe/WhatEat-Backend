package com.exe.whateat.application.restaurant_request.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.restaurant.mapper.RestaurantMapper;
import com.exe.whateat.application.restaurant_request.response.RequestResponse;
import com.exe.whateat.entity.request.RestaurantRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RequestMapper implements WhatEatMapper<RestaurantRequest, RequestResponse> {

    private RestaurantMapper restaurantMapper;

    @Override
    public RequestResponse convertToDto(RestaurantRequest restaurantRequest) {
        if (restaurantRequest == null) {
            return null;
        }
        final RequestResponse.RequestResponseBuilder builder = RequestResponse.builder()
                .tsid(restaurantRequest.getId().asTsid())
                .content(restaurantRequest.getContent())
                .title(restaurantRequest.getTitle())
                .type(restaurantRequest.getType())
                .createdAt(restaurantRequest.getCreatedAt())
                .restaurant(restaurantMapper.convertToDto(restaurantRequest.getRestaurant()));
        return builder.build();
    }
}
