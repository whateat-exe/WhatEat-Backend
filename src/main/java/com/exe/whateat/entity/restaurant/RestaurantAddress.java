package com.exe.whateat.entity.restaurant;

import com.exe.whateat.entity.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant_address")
public class RestaurantAddress extends AbstractEntity {

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "province_id", nullable = false)
    private Integer provinceId;

    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Column(name = "ward_id", nullable = false)
    private Integer wardId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
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
