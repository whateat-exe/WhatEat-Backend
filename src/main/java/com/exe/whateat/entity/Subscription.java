package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private double price;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private int duration;
    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "Subscription", fetch = FetchType.LAZY)
    private List<RestaurantSubscription> RestaurantSubscriptions;
}
