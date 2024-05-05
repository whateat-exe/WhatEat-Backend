package com.exe.whateat.entity.request;

import com.exe.whateat.entity.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
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
@Table(name = "restaurant_response")
public class RestaurantRequestResponse extends AbstractEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @OneToOne(optional = false)
    @JoinColumn(name = "restaurant_request_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private RestaurantRequest restaurantRequest;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
