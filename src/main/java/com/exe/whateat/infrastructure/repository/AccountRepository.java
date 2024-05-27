package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, WhatEatId> {

    Optional<Account> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query(value =
            """
            SELECT ac FROM Account ac
            WHERE ac.status = 'PENDING' AND ac.id < ?1
           """)
    List<Account> findAllByStatusPendingForDelete(WhatEatId id);
}