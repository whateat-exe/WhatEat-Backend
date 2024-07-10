package com.exe.whateat.application.subscription.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.subscription.response.UserSubscriptionResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackerResponse;
import com.exe.whateat.entity.subscription.UserSubscription;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
                .paidDate(convertToDate(userSubscriptionTracker.getValidityStart()))
                .subscription(mapper.convertToDto(userSubscriptionTracker.getSubscription()))
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
