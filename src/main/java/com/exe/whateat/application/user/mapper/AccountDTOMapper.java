package com.exe.whateat.application.user.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.entity.account.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDTOMapper implements WhatEatMapper<Account, UserResponse> {

    @Override
    public UserResponse convertToDto(Account account) {
        return UserResponse.builder()
                .id(account.getId().asTsid())
                .email(account.getEmail())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .status(account.getStatus().toString())
                .role(account.getRole().name())
                .imageUrl(account.getImage())
                .build();
    }
}
