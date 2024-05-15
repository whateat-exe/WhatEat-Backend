package com.exe.whateat.infrastructure.security;

import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class WhatEatSecurityHelper {

    public Optional<Account> getCurrentLoggedInAccount() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (!(authentication.getPrincipal() instanceof Account account)) {
            return Optional.empty();
        }
        return Optional.of(account);
    }

    public boolean currentAccountIsAdminOrManager() {
        final Optional<Account> account = getCurrentLoggedInAccount();
        if (account.isEmpty()) {
            return false;
        }
        final AccountRole role = account.get().getRole();
        return ((role == AccountRole.ADMIN) || (role == AccountRole.MANAGER));
    }

    public boolean currentAccountIsNotAdminOrManager() {
        return !currentAccountIsAdminOrManager();
    }
}
