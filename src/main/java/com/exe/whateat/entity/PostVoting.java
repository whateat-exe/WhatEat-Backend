package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PostVoting")
public class PostVoting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "PostId")
    private Post Post;

    @ManyToOne
    @JoinColumn(name = "AccountId")
    private Account Account;
}
