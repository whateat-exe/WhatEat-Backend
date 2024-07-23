package com.exe.whateat.entity.food;

import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.common.Money;
import com.exe.whateat.entity.random.RandomHistoryDish;
import com.exe.whateat.entity.random.Rating;
import com.exe.whateat.entity.restaurant.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dish")
public class Dish extends AbstractEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @Embedded
    private Money price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DishStatus status;

    @ManyToOne
    @JoinColumn(name = "food_id", columnDefinition = AbstractEntity.ID_COLUMN, nullable = false)
    private Food food;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", columnDefinition = AbstractEntity.ID_COLUMN, nullable = false)
    private Restaurant restaurant;

    @OneToMany(mappedBy = "dish")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "dish")
    private List<RandomHistoryDish> randomHistoryDishes;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
