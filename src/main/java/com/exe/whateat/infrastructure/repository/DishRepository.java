package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DishRepository extends JpaRepository<Dish, WhatEatId> {

    @Query(value = """ 
                SELECT d FROM Dish d
                JOIN FETCH d.food
                JOIN FETCH d.restaurant
                WHERE d.id = ?1
            """)
    Dish findByIdOfDish(WhatEatId whatEatId);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.dish.id = :dishId")
    Long countRatingsByDishId(@Param("dishId") WhatEatId dishId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.dish.id = :dishId")
    Double findAverageRatingByDishId(@Param("dishId") WhatEatId dishId);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.dish.id = :dishId AND r.stars = :stars")
    Long countRatingsByDishIdAndStars(@Param("dishId") WhatEatId dishId, @Param("stars") int stars);

    long countByRestaurantId(WhatEatId id);

    long countByStatusAndRestaurantId(ActiveStatus status, WhatEatId id);

    @Query(value = """
                   UPDATE dish 
                   SET status = 'EXPIRED'
                   WHERE status = 'ACTIVE' AND 
                   restaurant_id 
                   IN (
                       SELECT DISTINCT d.restaurant_id
                       FROM dish d 
                       LEFT JOIN restaurant_subscription_tracker rst
                       ON d.restaurant_id = rst.restaurant_id
                       GROUP BY d.restaurant_id
                       HAVING SUM(CASE WHEN rst.subscription_status = 'ACTIVE' THEN 1 ELSE 0 END) = 0
                   );
                   """,
            nativeQuery = true)
    @Modifying
    void changeAllExpiredDish();

    @Modifying
    @Query(value = """
                   UPDATE dish d
                   SET status = 'EXPIRED'
                   WHERE d.restaurant_id = :restaurantId AND d.status = 'ACTIVE' 
                   AND d.id
                   NOT IN (
                       SELECT sub.id FROM dish sub 
                       WHERE sub.restaurant_id = :restaurantId AND sub.status = 'ACTIVE' 
                       ORDER BY sub.id DESC 
                       LIMIT :maxActiveDishes
                   );
                   """,
            nativeQuery = true)
    void expireExceedingActiveDishes(@Param("maxActiveDishes") int maxActiveDishes, @Param("restaurantId") Long restaurantId);
}