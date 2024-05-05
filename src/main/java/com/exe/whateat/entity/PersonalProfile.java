package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PersonalProfile")
public class PersonalProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String type;

    @ManyToOne
    @JoinColumn(name = "AccountId")
    private Account Account;

    @ManyToOne
    @JoinColumn(name = "TagId")
    private Tag Tag;
}
