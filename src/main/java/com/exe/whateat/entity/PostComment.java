package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PostComment")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String Content;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "PostId")
    private Post Post;

    @ManyToOne
    @JoinColumn(name = "AccountId")
    private Account Account;
}
