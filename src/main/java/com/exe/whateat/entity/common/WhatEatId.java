package com.exe.whateat.entity.common;

import io.github.x4ala1c.tsid.Tsid;
import io.github.x4ala1c.tsid.TsidGenerator;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.NonNull;

import java.util.Objects;

@Setter
@SuperBuilder
@NoArgsConstructor
@Embeddable
@SuppressWarnings("unused")
public final class WhatEatId implements Comparable<WhatEatId> {

    private Tsid id;

    public WhatEatId(Tsid id) {
        if (id == null) {
            throw new NullPointerException("ID is null");
        }
        this.id = id;
    }

    public static WhatEatId generate() {
        return new WhatEatId(TsidGenerator.globalGenerate());
    }

    public Tsid asTsid() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhatEatId whatEatId = (WhatEatId) o;
        return Objects.equals(id, whatEatId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return id.asString();
    }

    @Override
    public int compareTo(@NonNull WhatEatId o) {
        return id.compareTo(o.id);
    }
}
