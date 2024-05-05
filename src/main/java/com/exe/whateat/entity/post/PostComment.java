package com.exe.whateat.entity.post;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.AbstractAuditableEntity;
import com.exe.whateat.entity.common.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "post_comment")
public class PostComment extends AbstractAuditableEntity {

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Post post;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Account account;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
