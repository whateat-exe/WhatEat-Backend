package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, WhatEatId> {

    Boolean existsByNameIgnoreCase(String name);

    @Query(
            value = """
                    SELECT f FROM Food f
                    LEFT JOIN FETCH f.parentFood
                    WHERE f.id = ?1
                    """
    )
    Optional<Food> findByIdWithParent(WhatEatId id);

    @Query(
            value = """
                    WITH RECURSIVE FoodCircularRef AS (
                        SELECT fr.id AS id, fr.parent_food_id AS parentFoodId, (fr.id = ?1) AS isCircular
                        FROM food fr
                        WHERE fr.id = ?2 AND fr.parent_food_id IS NOT NULL
                        UNION ALL
                        SELECT f.id AS id, f.parent_food_id AS parentFoodId, f.id = ?1 AS isCircular
                        FROM food f
                        JOIN FoodCircularRef fcr
                        ON f.id = fcr.parentFoodId
                        WHERE NOT fcr.isCircular
                    )
                    SELECT EXISTS (
                        SELECT 1 WHERE ?1 = ?2
                        UNION ALL
                        SELECT 1 FROM FoodCircularRef fcr
                        WHERE fcr.isCircular
                    )
                    """,
            nativeQuery = true
    )
    Boolean parentFoodIsNotValidToSet(long id, long parentFoodId);
}