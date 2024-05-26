package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, WhatEatId> {

    Optional<Account> findByEmail(String email);

    Boolean existsByEmail(String email);
}