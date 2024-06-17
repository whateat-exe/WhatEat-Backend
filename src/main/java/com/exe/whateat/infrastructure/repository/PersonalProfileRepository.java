package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.profile.PersonalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PersonalProfileRepository extends JpaRepository<PersonalProfile, WhatEatId> {

    List<PersonalProfile> findByAccountId(WhatEatId accountId);

    void deleteAllByAccountIdAndIdIn(WhatEatId accountId, Collection<WhatEatId> ids);

    void deleteAllByAccountId(WhatEatId accountId);
}