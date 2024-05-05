package com.exe.whateat.entity.random;

import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.food.Dish;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "random_history_dish")
public class RandomHistoryDish extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "random_history_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private RandomHistory randomHistory;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Dish dish;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
