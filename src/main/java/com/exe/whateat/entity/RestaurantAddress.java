package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "RestaurantAddress")
public class RestaurantAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    private int provinceId;
    private int districtId;
    private int wardId;

    @OneToOne
    @JoinColumn(name = "RestaurantId")
    private Restaurant Restaurant;
}
