package com.exe.whateat.entity.request;

import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.restaurant.Restaurant;
import jakarta.persistence.Column;
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
@Table(name = "restaurant_request_tracker")
public class RequestCreateTracker extends AbstractEntity {

    @Column(name = "max_number_of_create_dish")
    private int maxNumberOfCreateDish;

    @Column(name = "number_requested_dish")
    private int numberOfRequestedDish;

    @Column(name = "validity_start")
    private Instant validityStart;

    @Column(name = "validity_end")
    private Instant validityEnd;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestCreateTrackerStatus requestCreateTrackerStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Restaurant restaurant;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
