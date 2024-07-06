package com.exe.whateat.application.subscription.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.subscription.response.UserSubscriptionResponse;
import com.exe.whateat.entity.subscription.UserSubscription;
import org.springframework.stereotype.Component;

@Component
public class UserSubscriptionMapper implements WhatEatMapper<UserSubscription, UserSubscriptionResponse> {

    @Override
    public UserSubscriptionResponse convertToDto(UserSubscription userSubscription) {
        if (userSubscription == null) {
            return null;
        }
        return UserSubscriptionResponse.builder()
                .price(userSubscription.getPrice().getAmount())
                .id(userSubscription.getId().asTsid())
                .description(userSubscription.getDescription())
                .status(userSubscription.getStatus())
                .duration(userSubscription.getDuration())
                .name(userSubscription.getName())
                .build();
    }
}
