package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantRepository extends JpaRepository<Restaurant, WhatEatId> {

    @Query(
            value = """
                    SELECT EXISTS (
                        SELECT 1 FROM Restaurant r WHERE r.name = :restaurantName
                        UNION ALL
                        SELECT 1 FROM Account a WHERE a.email = :accountEmail
                    )
                    """
    )
    Boolean existsByNameAndAccountEmail(@Param("restaurantName") String restaurantName,
                                        @Param("accountEmail") String accountEmail);

    Boolean existsByName(String name);
}
