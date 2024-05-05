package com.exe.whateat.entity.subscription;

import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscription")
public class Subscription extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 5000)
    private String description;

    @Embedded
    private Money price;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private SubscriptionType type;

    /**
     * Must be in seconds.
     */
    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActiveStatus status;

    @OneToMany(mappedBy = "subscription")
    private List<RestaurantSubscription> restaurantSubscriptions;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
