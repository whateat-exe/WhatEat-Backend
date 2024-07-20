package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.PostVoting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PostVotingRepository extends JpaRepository<PostVoting, WhatEatId> {

    @Query(
            value = """
                        SELECT pv FROM PostVoting pv
                        WHERE pv.account.id = ?1 AND pv.post.id = ?2
                    """
    )
    Optional<PostVoting> postVotingAlreadyExists(WhatEatId accountId, WhatEatId postId);

    @Query(value = """
            SELECT COUNT(*) FROM post_voting
            WHERE CAST(TO_TIMESTAMP((:epoch + (id >> 22)) / 1000.0) AS TIMESTAMP) BETWEEN :start AND :end
            """,
            nativeQuery = true)
    long countRecordsInRange(@Param("start") Instant start, @Param("end") Instant end, @Param("epoch") long epoch);
}