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
                    INNER JOIN food_tag ft
                        ON f.id = ft.food_id
                    WHERE ft.tag_id IN (
                        SELECT pp.tag_id FROM personal_profile pp
                        WHERE pp.account_id = ?1 AND pp.type = 'LIKE'
                    )
                    GROUP BY f.id
                    HAVING count(DISTINCT ft.tag_id) = (
                        SELECT COUNT(DISTINCT pp2.tag_id)
                        FROM personal_profile pp2
                        WHERE pp2.account_id = ?1 AND pp2.type = 'LIKE'
                    )
                    ORDER BY random()
                    LIMIT 10
                    """,
            nativeQuery = true
    )
    List<Food> random(Long accountId);
}