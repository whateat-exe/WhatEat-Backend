package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.FoodTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTagRepository extends JpaRepository<FoodTag, WhatEatId> {
}