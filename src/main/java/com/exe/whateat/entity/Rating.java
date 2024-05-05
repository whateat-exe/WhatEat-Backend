package com.exe.whateat.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    @Min(value = 1, message = "Value must be greater than or equal to 1")
    @Max(value = 5, message = "Value must be less than or equal to 5")
    private int stars;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String feedback;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "DishId")
    private Dish Dish;
    @OneToOne
    @JoinColumn(name = "AccountId")
    private Account Account;
}
