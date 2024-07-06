package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.subscription.RestaurantSubscription;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantSubscriptionRepository extends JpaRepository<RestaurantSubscription, WhatEatId> {

    Optional<RestaurantSubscription> findByType(RestaurantSubscriptionType type);
}