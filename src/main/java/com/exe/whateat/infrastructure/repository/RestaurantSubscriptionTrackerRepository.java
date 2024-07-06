package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RestaurantSubscriptionTrackerRepository extends JpaRepository<RestaurantSubscriptionTracker, WhatEatId> {

    @Query(
            value = """
                    SELECT generate_restaurant_order_code()
                    """
    )
    Integer generateOrderCode();

    Optional<RestaurantSubscriptionTracker> findByPaymentIdAndOrderCode(String paymentLink, Integer orderCode);

    @Modifying
    @Query(
            value = """
                    UPDATE RestaurantSubscriptionTracker t
                    SET t.subscriptionStatus = 'CANCELLED', t.validityEnd = ?2
                    WHERE t.restaurant.id = ?1 AND t.subscriptionStatus = 'ACTIVE' AND t.validityEnd < ?2
                    """
    )
    void cancelAllCurrentlyActiveSubscriptions(WhatEatId restaurantId, Instant validityEnd);

    List<RestaurantSubscriptionTracker> findAllByRestaurantId(WhatEatId restaurantId, Pageable pageable);

    long countByRestaurantId(WhatEatId restaurantId);
}