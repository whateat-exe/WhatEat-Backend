package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RandomHistory")
public class RandomHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "AccountId")
    private Account Account;

    @OneToOne
    @JoinColumn(name = "FoodId")
    private Food Food;

    @OneToMany(mappedBy = "RandomHistory")
    private List<RandomHistoryDish> RandomHistoryDishes;
}
