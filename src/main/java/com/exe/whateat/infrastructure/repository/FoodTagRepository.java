package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.food.FoodTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodTagRepository extends JpaRepository<FoodTag, WhatEatId> {

    List<FoodTag> findByFood(Food food);

    @Query("SELECT t FROM FoodTag t JOIN FETCH t.food JOIN FETCH t.tag WHERE t.id = :id")
    FoodTag FindByFoodTag_Id(@Param("id") WhatEatId id);
}