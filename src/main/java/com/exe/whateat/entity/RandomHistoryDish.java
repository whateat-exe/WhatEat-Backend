package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RandomHistoryDish")
public class RandomHistoryDish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "RandomHistoryId")
    private RandomHistory RandomHistory;
    @ManyToOne
    @JoinColumn(name = "DishId")
    private Dish Dish;

}
