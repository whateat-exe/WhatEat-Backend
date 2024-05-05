package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "AccountId")
    private Account Account;

    @OneToMany(mappedBy = "Post")
    private List<PostImage> PostImages;

    @OneToMany(mappedBy = "Post")
    private List<PostVoting> PostVotings;

    @OneToMany(mappedBy = "Post")
    private List<PostComment> PostComments;
}
