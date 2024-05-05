package com.exe.whateat.entity.profile;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.AbstractEntity;
import com.exe.whateat.entity.food.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "personal_profile")
public class PersonalProfile extends AbstractEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tag_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Tag tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProfileType type;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
