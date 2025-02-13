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

    boolean existsByPaymentId(String paymentId);

    UserSubscriptionTracker findByPaymentId(String paymentId);

    @Query(value = """
             UPDATE user_subscription_tracker 
             SET payment_status = 'EXPIRED', subscription_status = 'CANCELLED'
             WHERE payment_status = 'PENDING' and subscription_status = 'PENDING'
             AND expiration_time < NOW() AT TIME ZONE 'UTC'
            """, nativeQuery = true)
    @Modifying
    void changeAllExpiredPayment();

    @Query(value = """
                   UPDATE user_subscription_tracker 
                   SET subscription_status = 'EXPIRED'
                   WHERE subscription_status = 'ACTIVE'
                   AND validity_end < NOW() AT TIME ZONE 'UTC'
                   """,
            nativeQuery = true)
    @Modifying
    void changeAllExpiredSubscription();
}