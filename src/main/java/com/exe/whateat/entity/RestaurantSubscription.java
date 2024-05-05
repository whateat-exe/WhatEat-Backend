package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "RestaurantSubscription")
public class RestaurantSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private boolean status;
    @Column(nullable = false)
    private LocalDateTime activationDate;
    @Column(nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "SubcriptionId")
    private Subscription Subscription;
    @ManyToOne
    @JoinColumn(name = "RestaurantId")
    private Restaurant Restaurant;

    @OneToMany(mappedBy = "RestaurantSubscription")
    private List<RestaurantSubscriptionPayment> RestaurantSubscriptionPayments;
}
