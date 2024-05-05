package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.random.RandomHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomHistoryRepository extends JpaRepository<RandomHistory, WhatEatId> {
}