package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.Tag;
import io.github.x4ala1c.tsid.Tsid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodTagRepository extends JpaRepository<FoodTag, WhatEatId> {

    Optional<FoodTag> findByFood_Id(Tsid tsid);

    @Query("SELECT t FROM FoodTag t JOIN FETCH t.food JOIN FETCH t.tag WHERE t.id = :id")
    FoodTag FindByFoodTag_Id (@Param("id") WhatEatId id);
}