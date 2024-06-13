package com.exe.whateat.entity.post;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.AbstractAuditableEntity;
import com.exe.whateat.entity.common.AbstractEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post extends AbstractAuditableEntity {

    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", columnDefinition = AbstractEntity.ID_COLUMN)
    private Account account;

    /**
     * Maximum 3 images only.
     */
    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostImage> postImages;

    /**
     * Must be lazying loaded, and calculations of votes should be handled on database level.
     */
    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostVoting> postVoting;

    /**
     * Should be paginated.
     */
    @OneToMany(mappedBy = "post")
    private List<PostComment> postComments;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
