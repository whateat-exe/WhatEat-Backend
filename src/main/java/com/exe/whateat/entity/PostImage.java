package com.exe.whateat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PostImage")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String caption;
    @Column(nullable = false)
    private String Image;

    @ManyToOne
    @JoinColumn(name = "PostId")
    private Post Post;

}
