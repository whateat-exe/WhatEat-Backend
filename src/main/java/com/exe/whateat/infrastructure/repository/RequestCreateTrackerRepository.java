package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RequestCreateTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RequestCreateTrackerRepository extends JpaRepository<RequestCreateTracker, WhatEatId> {

    @Query(value = """
                select rct from RequestCreateTracker rct
                where rct.restaurant.id = ?1
                and rct.requestCreateTrackerStatus = 'ACTIVE'
        """)
    Optional<RequestCreateTracker> findByRestaurantIdAndStatus(WhatEatId whatEatId);
}
