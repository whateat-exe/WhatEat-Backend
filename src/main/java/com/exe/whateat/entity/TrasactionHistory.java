package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TrasactionHistory")
public class TrasactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type;
    private double paidAmount;
    private boolean status;
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "TrasactionHistory", fetch = FetchType.LAZY)
    private RestaurantSubscriptionPayment RestaurantSubscriptionPayment;
}
