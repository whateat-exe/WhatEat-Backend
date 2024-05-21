package com.exe.whateat.entity.account;

import com.exe.whateat.entity.common.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_verify")
public class AccountVerify extends AbstractEntity {

    @Column(name = "verify_code", nullable = false, length = 6)
    private String verifiedCode;

    @Column(name = "create_at", nullable = false)
    private Instant CreatedAt;

    @Column(name = "last_mofified", nullable = false)
    private Instant LastModified;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "account_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Account account;
}
