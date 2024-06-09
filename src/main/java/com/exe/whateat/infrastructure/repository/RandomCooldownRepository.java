package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.random.RandomCooldown;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomCooldownRepository extends JpaRepository<RandomCooldown, WhatEatId> {
}