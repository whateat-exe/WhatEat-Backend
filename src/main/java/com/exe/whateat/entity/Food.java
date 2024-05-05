package com.exe.whateat.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String image;
    @Column(nullable = false)
    private boolean status;
    @OneToMany(mappedBy = "Food")
    private List<Dish> Dishes;
    @ManyToOne(optional = false)
    @Nullable
    private Food Food;
    @OneToMany(mappedBy = "Food", fetch = FetchType.LAZY)
    private List<FoodTag> FoodTags;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "Food")
    private RandomHistory RandomHistory;
}
