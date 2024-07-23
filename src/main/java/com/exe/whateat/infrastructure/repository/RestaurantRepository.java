package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.restaurant.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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
    boolean existsByNameAndAccountEmail(@Param("restaurantName") String restaurantName,
                                        @Param("accountEmail") String accountEmail);

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            select r from Restaurant r
            where r.account.id = ?1
        """)
    Optional<Restaurant> findByAccountId(WhatEatId id);
}
