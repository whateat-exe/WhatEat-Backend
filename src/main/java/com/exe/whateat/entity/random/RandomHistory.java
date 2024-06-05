package com.exe.whateat.entity.random;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.food.Food;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "random_history")
public class RandomHistory extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Account account;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Food food;

    @OneToMany(mappedBy = "randomHistory")
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
