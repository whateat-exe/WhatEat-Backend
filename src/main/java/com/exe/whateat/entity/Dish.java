package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Dish")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String image;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "FoodId")
    private Food Food;

    @ManyToOne
    @JoinColumn(name = "RestaurantId")
    private Restaurant Restaurant;

    @OneToMany(mappedBy = "Dish")
    private List<Rating> Ratings;

    @OneToMany(mappedBy = "Dish")
    private List<RandomHistoryDish> RandomHistoryDishes;
}
