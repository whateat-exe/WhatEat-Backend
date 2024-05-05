package com.exe.whateat.entity.subscription;

import com.exe.whateat.entity.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "restaurant_subscription_payment")
public class RestaurantSubscriptionPayment extends AbstractEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "payment_time", nullable = false)
    private Instant paymentTime;

    @ManyToOne
    @JoinColumn(name = "restaurant_subscription_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private RestaurantSubscription restaurantSubscription;

    @OneToOne
    @JoinColumn(name = "transaction_history_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private TransactionHistory transactionHistory;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
