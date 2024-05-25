package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UsersResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.account.QAccount;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetUsersByRole {

    @Data
    public class GetUsersByRoleRequest extends PaginationRequest {

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "Get users by role"
    )
    public static class GetUsersByRoleController extends AbstractController {

        private GetUsersByRoleService getUsersByRoleService;

        @Operation(
                summary = "Get users by role API. Returns the list of users paginated."
        )
        @ApiResponse(
                description = "Successful. Returns list of the users.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = UsersResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the users.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/users/role/{accountRole}")
        public ResponseEntity<Object> getUserByRole(@Valid GetUsersByRoleRequest getUsersByRoleRequest, @PathVariable AccountRole accountRole) {
            var response = getUsersByRoleService.getUsersByRole(getUsersByRoleRequest, accountRole);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetUsersByRoleService {

        private AccountRepository accountRepository;
        private EntityManager entityManager;
        private AccountDTOMapper accountDTOMapper;

        public UsersResponse getUsersByRole(GetUsersByRoleRequest getUsersByRoleRequest, AccountRole accountRole) {
            final QAccount qAccount = QAccount.account;
            final JPAQuery<Account> accountJPAQuery = new JPAQuery<>(entityManager)
                    .select(qAccount)
                    .from(qAccount)
                    .where(qAccount.role.eq(accountRole))
                    .limit(getUsersByRoleRequest.getLimit())
                    .offset(getUsersByRoleRequest.getOffset());
            final List<Account> accounts = accountJPAQuery.fetch();
            final long total = accountRepository.count();
            final UsersResponse response = new UsersResponse(accounts.stream().map(accountDTOMapper::convertToDto).toList(), total);
            response.setPage(getUsersByRoleRequest.getPage());
            response.setLimit(getUsersByRoleRequest.getLimit());
            return response;
        }
    }
}
