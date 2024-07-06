package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserSubscriptionTrackerRepository extends JpaRepository<UserSubscriptionTracker, WhatEatId> {

    Optional<UserSubscriptionTracker> findByPaymentIdAndOrderCode(String paymentId, Integer orderCode);

    @Modifying
    @Query(
            value = """
                    UPDATE UserSubscriptionTracker t
                    SET t.subscriptionStatus = 'CANCELLED', t.validityEnd = ?2
                    WHERE t.user.id = ?1 AND t.subscriptionStatus = 'ACTIVE' AND t.validityEnd < ?2
                    """
    )
    void cancelAllCurrentlyActiveSubscriptions(WhatEatId userId, Instant validityEnd);

    @Query(
            value = """
                    SELECT generate_user_order_code()
                    """
    )
    Integer generateOrderCode();

    @Query(
            value = """
                    SELECT EXISTS (
                        SELECT 1 FROM UserSubscriptionTracker t WHERE t.user.id = ?1 AND t.subscriptionStatus = 'ACTIVE'
                    )
                    """
    )
    boolean userIsUnderActiveSubscription(WhatEatId userId);

    List<UserSubscriptionTracker> findAllByUserId(WhatEatId userId, Pageable pageable);

    long countByUserId(WhatEatId userId);
}