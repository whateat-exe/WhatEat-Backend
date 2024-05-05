package com.exe.whateat.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractAuditableEntity extends AbstractEntity {

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "last_modified")
    private Instant lastModified;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
