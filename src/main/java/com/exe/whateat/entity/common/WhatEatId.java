package com.exe.whateat.entity.common;

import io.github.x4ala1c.tsid.Tsid;
import io.github.x4ala1c.tsid.TsidGenerator;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Setter
@SuperBuilder
@NoArgsConstructor
@Embeddable
@SuppressWarnings("unused")
public final class WhatEatId {

    private Tsid id;

    private WhatEatId(Tsid id) {
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
}
