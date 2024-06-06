package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DishRepository extends JpaRepository<Dish, WhatEatId> {

    @Query(value = """ 
                SELECT d FROM Dish d
                JOIN FETCH d.food
                JOIN FETCH d.restaurant
                WHERE d.id = ?1
            """)
    Dish findByIdOfDish(WhatEatId whatEatId);

    boolean existsByNameIgnoreCase(String name);
}