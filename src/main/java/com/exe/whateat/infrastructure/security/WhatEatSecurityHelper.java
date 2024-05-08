package com.exe.whateat.infrastructure.security;

import com.exe.whateat.entity.account.Account;
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
}
