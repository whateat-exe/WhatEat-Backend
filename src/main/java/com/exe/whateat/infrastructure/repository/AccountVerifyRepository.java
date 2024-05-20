package com.exe.whateat.infrastructure.repository;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountVerify;
import com.exe.whateat.entity.common.WhatEatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountVerifyRepository extends JpaRepository<AccountVerify, WhatEatId> {

    Optional<AccountVerify> findByAccount(Account account);
}
