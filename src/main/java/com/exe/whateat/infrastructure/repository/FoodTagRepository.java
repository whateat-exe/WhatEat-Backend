package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.FoodTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FoodTagRepository extends JpaRepository<FoodTag, WhatEatId> {

    @Query(
            value = """
                    SELECT t FROM FoodTag t
                    JOIN FETCH t.food
                    JOIN FETCH t.tag
                    WHERE t.id = :id
                    """
    )
    Optional<FoodTag> findByIdPopulated(@Param("id") WhatEatId id);

    @Query(
            value = """
                    SELECT EXISTS (
                        SELECT 1 FROM FoodTag ft
                        WHERE ft.food.id = ?1 AND ft.tag.id = ?2
                    )
                    """
    )
    boolean foodTagAlreadyExists(WhatEatId foodId, WhatEatId tagId);

}