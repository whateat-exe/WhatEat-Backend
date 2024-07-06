package com.exe.whateat.entity.subscription;

import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.common.Money;
import com.exe.whateat.entity.restaurant.Restaurant;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant_subscription_tracker")
public class RestaurantSubscriptionTracker extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Restaurant restaurant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subscription_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private RestaurantSubscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private PaymentProvider provider;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

    @Column(name = "order_code", nullable = false)
    private Integer orderCode;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false, precision = 19, scale = 3))
    private Money amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "signature", nullable = false, length = 512)
    private String signature;

    @Column(name = "validity_start")
    private Instant validityStart;

    @Column(name = "validity_end")
    private Instant validityEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus;
}
