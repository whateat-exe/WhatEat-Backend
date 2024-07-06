package com.exe.whateat.application.subscription.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionResponse;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import org.springframework.stereotype.Component;

@Component
public class RestaurantSubscriptionMapper implements WhatEatMapper<RestaurantSubscription, RestaurantSubscriptionResponse> {

    @Override
    public RestaurantSubscriptionResponse convertToDto(RestaurantSubscription restaurantSubscription) {
        if (restaurantSubscription == null) {
            return null;
        }
        return RestaurantSubscriptionResponse.builder()
                .price(restaurantSubscription.getPrice().getAmount())
                .name(restaurantSubscription.getName())
                .type(restaurantSubscription.getType())
                .description(restaurantSubscription.getDescription())
                .status(restaurantSubscription.getStatus())
                .duration(restaurantSubscription.getDuration())
                .id(restaurantSubscription.getId().asTsid())
                .build();
    }
}
