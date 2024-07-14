package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RequestCreateTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RequestCreateTrackerRepository extends JpaRepository<RequestCreateTracker, WhatEatId> {

    @Modifying
    @Query(value = """
                select * from request_create_tracker
                where id = ?1
                and status = 'ACTIVE'
        """, nativeQuery = true)
    Optional<RequestCreateTracker> findByRestaurantIdAndStatus(WhatEatId whatEatId);
}
