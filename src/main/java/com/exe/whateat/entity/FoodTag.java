package com.exe.whateat.entity;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.users.GenericRole;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FoodTag")
public class FoodTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "FoodId")
    private Food Food;

    @ManyToOne
    @JoinColumn(name = "TagId")
    private Tag Tag;
}
