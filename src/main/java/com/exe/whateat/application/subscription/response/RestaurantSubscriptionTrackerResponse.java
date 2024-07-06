package com.exe.whateat.application.subscription.response;

import com.exe.whateat.entity.subscription.PaymentProvider;
import com.exe.whateat.entity.subscription.SubscriptionStatus;
import io.github.x4ala1c.tsid.Tsid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSubscriptionTrackerResponse {

    private Tsid id;
    private Tsid restaurantId;
    private RestaurantSubscriptionResponse subscription;
    private PaymentProvider provider;
    private BigDecimal amount;
    private Instant validityStart;
    private Instant validityEnd;
    private SubscriptionStatus subscriptionStatus;
}
