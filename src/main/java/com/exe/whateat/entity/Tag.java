package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String type;

    @OneToMany(mappedBy = "Tag")
    private List<FoodTag> FoodTags;

    @OneToMany(mappedBy = "Tag")
    private List<PersonalProfile> PersonalProfiles;
}
