package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "RestaurantSubscriptionPayment")
public class RestaurantSubscriptionPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean status;
    private LocalDateTime paymentDate;
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "RestaurantSubscriptionId")
    private RestaurantSubscription RestaurantSubscription;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RestaurantSubscriptionPayment")
    private TrasactionHistory TrasactionHistory;
}
