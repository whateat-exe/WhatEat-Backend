package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "RestaurantResponse")
public class RestaurantRequestResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private boolean status;

    @OneToOne
    @JoinColumn(name = "AccountId")
    private RestaurantRequest RestaurantRequest;
}
