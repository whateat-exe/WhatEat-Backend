package com.exe.whateat.entity.restaurant;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.request.RequestCreateTracker;
import com.exe.whateat.entity.request.RestaurantRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "restaurant")
public class Restaurant extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false, length = 5000)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "account_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Account account;

    @OneToMany(mappedBy = "restaurant")
    private List<Dish> dishes;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "restaurant")
    private List<RestaurantRequest> restaurantRequests;

    @OneToMany(mappedBy = "restaurant")
    private List<RequestCreateTracker> requestCreateTrackers;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
