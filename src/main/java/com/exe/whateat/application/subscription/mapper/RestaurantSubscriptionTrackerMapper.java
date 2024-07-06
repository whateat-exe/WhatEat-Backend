package com.exe.whateat.application.subscription.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionResponse;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackerResponse;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestaurantSubscriptionTrackerMapper implements WhatEatMapper<RestaurantSubscriptionTracker, RestaurantSubscriptionTrackerResponse> {

    private final WhatEatMapper<RestaurantSubscription, RestaurantSubscriptionResponse> mapper;

    @Override
    public RestaurantSubscriptionTrackerResponse convertToDto(RestaurantSubscriptionTracker restaurantSubscriptionTracker) {
        if (restaurantSubscriptionTracker == null) {
            return null;
        }
        return RestaurantSubscriptionTrackerResponse.builder()
                .id(restaurantSubscriptionTracker.getId().asTsid())
                .restaurantId(restaurantSubscriptionTracker.getRestaurant().getId().asTsid())
                .amount(restaurantSubscriptionTracker.getAmount().getAmount())
                .subscriptionStatus(restaurantSubscriptionTracker.getSubscriptionStatus())
                .validityEnd(restaurantSubscriptionTracker.getValidityEnd())
                .validityStart(restaurantSubscriptionTracker.getValidityStart())
                .provider(restaurantSubscriptionTracker.getProvider())
                .subscription(mapper.convertToDto(restaurantSubscriptionTracker.getSubscription()))
                .build();
    }
}
