package com.exe.whateat.application.subscription.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.subscription.response.UserSubscriptionResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackerResponse;
import com.exe.whateat.entity.subscription.UserSubscription;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSubscriptionTrackerMapper implements WhatEatMapper<UserSubscriptionTracker, UserSubscriptionTrackerResponse> {

    private final WhatEatMapper<UserSubscription, UserSubscriptionResponse> mapper;

    @Override
    public UserSubscriptionTrackerResponse convertToDto(UserSubscriptionTracker userSubscriptionTracker) {
        if (userSubscriptionTracker == null) {
            return null;
        }
        return UserSubscriptionTrackerResponse.builder()
                .userId(userSubscriptionTracker.getUser().getId().asTsid())
                .subscriptionStatus(userSubscriptionTracker.getSubscriptionStatus())
                .id(userSubscriptionTracker.getId().asTsid())
                .validityStart(userSubscriptionTracker.getValidityStart())
                .validityEnd(userSubscriptionTracker.getValidityEnd())
                .amount(userSubscriptionTracker.getAmount().getAmount())
                .provider(userSubscriptionTracker.getProvider())
                .subscription(mapper.convertToDto(userSubscriptionTracker.getSubscription()))
                .build();
    }
}
