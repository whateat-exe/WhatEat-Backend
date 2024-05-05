package com.exe.whateat.entity;

import com.exe.whateat.entity.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Table(name = "Account", uniqueConstraints = @UniqueConstraint(columnNames = "Email"))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    @Column(nullable = false, name = "Email")
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private boolean status;
    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "Account")
    private Restaurant Restaurant;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "Account")
    private Rating Rating;

    @OneToMany(mappedBy = "Account")
    private List<PersonalProfile> PersonalProfiles;

    @OneToMany(mappedBy = "Account")
    private List<RandomHistory> RandomHistories;
    @OneToMany(mappedBy = "Account")
    private List<Post> Posts;
    @OneToMany(mappedBy = "Account")
    private List<PostVoting> PostVotings;

    @OneToMany(mappedBy = "Account")
    private List<PostComment> PostComments;
}
