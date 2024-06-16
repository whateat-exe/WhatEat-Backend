package com.exe.whateat.entity.random;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.AbstractAuditableEntity;
import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.food.Dish;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "rating")
public class Rating extends AbstractAuditableEntity {

    @ManyToOne
    @JoinColumn(name = "dish_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Dish dish;

    @OneToOne
    @JoinColumn(name = "account_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Account account;

    /**
     * Stars must be between 1 and 5.
     */
    @Column(name = "stars", nullable = false)
    private Integer stars;

//    @Column(name = "title", nullable = false)
//    private String title;

    @Column(name = "feedback", nullable = false, length = 1000)
    private String feedback;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
