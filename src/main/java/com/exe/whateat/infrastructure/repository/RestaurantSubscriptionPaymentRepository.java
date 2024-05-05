package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantSubscriptionPaymentRepository extends JpaRepository<RestaurantSubscriptionPayment, WhatEatId> {
}