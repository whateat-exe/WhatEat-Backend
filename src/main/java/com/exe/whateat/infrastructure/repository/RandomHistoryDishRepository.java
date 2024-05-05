package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.random.RandomHistoryDish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RandomHistoryDishRepository extends JpaRepository<RandomHistoryDish, WhatEatId> {
}