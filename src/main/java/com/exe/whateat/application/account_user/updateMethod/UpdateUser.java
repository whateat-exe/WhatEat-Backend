package com.exe.whateat.application.account_user.updateMethod;

import com.exe.whateat.application.account_user.mapper.AccountDTOMapper;
import com.exe.whateat.application.account_user.updateMethod.response.UpdateUserResponse;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUser {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for update a user with Id"
    )
    public static final class UpdateUserController extends AbstractController {

        private final UpdateUserService updateUserService;
        private final WhatEatSecurityHelper whatEatSecurityHelper;

        @PatchMapping("/users/{id}")
        public ResponseEntity<UpdateUserResponse> updateUser(
                @RequestBody Map<String, Object> fields, @PathVariable String id
        ) {

            if (fields == null) {
                return ResponseEntity.ok(null);
            }
            Optional<Account> account = whatEatSecurityHelper.getCurrentLoggedInAccount();
            if (account.isEmpty()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEA_0003)
                        .reason("Unauthenticated", "You have not logged in")
                        .build();
            }
            // Everyone should be able to update their Account ?
            if (account.get().getRole() != AccountRole.USER) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEA_0002)
                        .reason("Forbidden", "You are not privileged to do this function")
                        .build();
            }
            UpdateUserResponse userResponse = updateUserService.updateUser(fields, id);
            return ResponseEntity.ok(userResponse);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class UpdateUserService {

        private final AccountRepository accountRepository;
        private final AccountDTOMapper accountDTOMapper;

        public UpdateUserResponse updateUser(Map<String, Object> fields, String id) {

            WhatEatId whatEatId = WhatEatId.builder().id(Tsid.fromString(id)).build();
            Optional<Account> accountExisting = accountRepository.findById(whatEatId);
            if (accountExisting.isPresent()) {
                fields.forEach((key, value) -> {
                    Field field = ReflectionUtils.findField(Account.class, key);
                    // Are you sure about using reflection?
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, accountExisting.get(), value);
                });
                Account accountUpdated = accountRepository.save(accountExisting.get());
                return UpdateUserResponse
                        .builder()
                        .userDTO(accountDTOMapper.apply(accountUpdated))
                        .build();
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WEA_0007)
                    .reason("server", "Fault in internal server")
                    .build();
        }
    }

}
