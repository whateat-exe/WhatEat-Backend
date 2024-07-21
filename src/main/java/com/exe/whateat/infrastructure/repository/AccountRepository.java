package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, WhatEatId> {

    Optional<Account> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query(value = """
            SELECT ac.* FROM account ac
            inner join account_verify av
            on ac.id = av.account_id
            WHERE ac.status = 'PENDING'
            AND av.status = 'PENDING'
            AND (?1 - (av.id >> 22))  > ?2
            """, nativeQuery = true)
    List<Account> getAllAccountPendingExpired(Long present, Long exceed);

    @Query(value = """
                delete from account
                where id in ?1
            """, nativeQuery = true)
    @Modifying
    void deleteUnusedAccount(List<Long> ids);

    @Query(value = """
            SELECT COUNT(*) FROM account 
            WHERE CAST(TO_TIMESTAMP((:epoch + (id >> 22)) / 1000.0) AS TIMESTAMP) BETWEEN :start AND :end
            AND status != 'PENDING'
            """,
            nativeQuery = true)
    long countRecordsInRange(@Param("start") Instant start, @Param("end") Instant end, @Param("epoch") long epoch);

}