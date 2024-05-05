package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Restaurant")
public class Restaurant {
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
    private boolean status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "AccountId")
    private Account Account;

    @OneToMany(mappedBy = "Restaurant")
    private List<Dish> Dishes;

    @OneToOne(mappedBy = "Restaurant", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RestaurantAddress RestaurantAddress;

    @OneToMany(mappedBy = "Restaurant", fetch = FetchType.LAZY)
    private List<RestaurantRequest> RestaurantRequests;
    @OneToMany(mappedBy = "Restaurant", fetch = FetchType.LAZY)
    private List<RestaurantSubscription> RestaurantSubscriptions;
}
