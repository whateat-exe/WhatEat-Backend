package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "RestaurantRequest")
public class RestaurantRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "RestaurantId")
    private Restaurant Restaurant;

    @OneToOne(mappedBy = "RestaurantRequest", fetch = FetchType.LAZY)
    private RestaurantRequestResponse RestaurantRequestResponse;
}
