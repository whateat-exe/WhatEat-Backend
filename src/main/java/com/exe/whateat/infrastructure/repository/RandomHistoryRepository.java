package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.random.RandomHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface RandomHistoryRepository extends JpaRepository<RandomHistory, WhatEatId> {
    @Query(value = """
            SELECT COUNT(*) FROM random_history 
            WHERE CAST(TO_TIMESTAMP((:epoch + (id >> 22)) / 1000.0) AS TIMESTAMP) BETWEEN :start AND :end
            """,
            nativeQuery = true)
    long countRecordsInRange(@Param("start") Instant start, @Param("end") Instant end, @Param("epoch") long epoch);

}