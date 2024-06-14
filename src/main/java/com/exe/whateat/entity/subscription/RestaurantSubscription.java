package com.exe.whateat.entity.subscription;

import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.restaurant.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant_subscription")
public class RestaurantSubscription extends AbstractEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "subcription_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Subscription subscription;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;

    @Column(name = "activation_time", nullable = false)
    private Instant activationTime;

    @Column(name = "end_time")
    private Instant endTime;

    @OneToMany(mappedBy = "restaurantSubscription")
    private List<RestaurantSubscriptionPayment> payments;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
