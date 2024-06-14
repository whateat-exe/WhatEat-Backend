package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, WhatEatId> {

    boolean existsByNameIgnoreCase(String name);

    @Query(
            value = """
                    SELECT f FROM Food f
                    WHERE f.id = ?1
                    """
    )
    Optional<Food> findByIdWithParent(WhatEatId id);

    @Query(
            value = """
                    SELECT f.* FROM food f
                    ORDER BY random() LIMIT 10
                    """,
            nativeQuery = true
    )
    List<Food> random();
}