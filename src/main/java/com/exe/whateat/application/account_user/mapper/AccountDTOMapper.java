package com.exe.whateat.application.account_user.mapper;

import com.exe.whateat.application.account_user.dto.UserDTO;
import com.exe.whateat.entity.account.Account;
import org.springframework.stereotype.Component;


import java.util.function.Function;

@Component
public class AccountDTOMapper implements Function<Account, UserDTO> {

    @Override
    public UserDTO apply(Account account) {
        return UserDTO.builder()
                .id(account.getId().asTsid())
                .email(account.getEmail())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .status(account.getStatus().toString())
                .role(account.getRole().name())
                .build();
    }
}
