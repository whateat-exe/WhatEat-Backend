package com.exe.whateat.application.subscription.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionResponse;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackerResponse;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
                .paidDate(convertToDate(restaurantSubscriptionTracker.getValidityStart()))
                .subscription(mapper.convertToDto(restaurantSubscriptionTracker.getSubscription()))
                .build();
    }

    private String convertToDate(Instant epochInstant) {
        if (epochInstant == null) return null;
        LocalDateTime dateTime = LocalDateTime.ofInstant(epochInstant, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - EEEE", new Locale("vi", "VN"));
        String formattedDate = dateTime.format(formatter);
        return formattedDate;
    }
}
