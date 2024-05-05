package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantSubscriptionRepository extends JpaRepository<RestaurantSubscription, WhatEatId> {
}