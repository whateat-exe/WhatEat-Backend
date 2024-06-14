package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.profile.PersonalProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalProfileRepository extends JpaRepository<PersonalProfile, WhatEatId> {
}