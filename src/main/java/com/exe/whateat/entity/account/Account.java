package com.exe.whateat.entity.account;

import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.post.PostComment;
import com.exe.whateat.entity.post.PostVoting;
import com.exe.whateat.entity.profile.PersonalProfile;
import com.exe.whateat.entity.random.RandomHistory;
import com.exe.whateat.entity.restaurant.Restaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
@SuppressWarnings("java:S1948")     // Disable serializable warnings because of using UserDetails
public class Account extends AbstractEntity implements UserDetails {

    @NaturalId
    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false, unique = true)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "image")
    private String image;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActiveStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AccountRole role;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToOne(mappedBy = "account")
    private Restaurant restaurant;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<PersonalProfile> personalProfiles;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<RandomHistory> randomHistories;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Post> posts;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<PostVoting> postVoting;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<PostComment> postComments;

    @OneToMany(mappedBy = "account")
    private List<AccountVerify> accountVerify;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return (status != ActiveStatus.PENDING);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return (status == ActiveStatus.ACTIVE);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
