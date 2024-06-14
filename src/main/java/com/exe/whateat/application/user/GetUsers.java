package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UsersResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.account.QAccount;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetUsers {

    @Getter
    @Setter
    public static final class GetUsersRequest extends PaginationRequest {

        private String fullName;
        private ActiveStatus status;
        private AccountRole role;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for user accounts."
    )
    public static class GetUsersController extends AbstractController {

        private final GetUsersService getUsersService;

        @GetMapping("/users")
        @Operation(
                summary = "Get users API. Returns the list of users paginated."
        )
        @ApiResponse(
                description = "Successful. Returns list of the users.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = UsersResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the foods.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getAllAccount(@ParameterObject GetUsersRequest request) {
            final UsersResponse response = getUsersService.getAllUser(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetUsersService {

        private final AccountRepository accountRepository;
        private final AccountDTOMapper accountDTOMapper;
        private final EntityManager entityManager;

        public UsersResponse getAllUser(GetUsersRequest request) {
            final QAccount qAccount = QAccount.account;
            BooleanExpression predicates = qAccount.isNotNull();
            if (StringUtils.isNotBlank(request.getFullName())) {
                predicates = predicates.and(qAccount.fullName.containsIgnoreCase(request.getFullName()));
            }
            if (request.getStatus() != null) {
                predicates = predicates.and(qAccount.status.eq(request.getStatus()));
            }
            if (request.getRole() != null) {
                predicates = predicates.and(qAccount.role.eq(request.getRole()));
            }
            final JPAQuery<Account> accountJPAQuery = new JPAQuery<>(entityManager)
                    .select(qAccount)
                    .from(qAccount)
                    .where(predicates)
                    .limit(request.getLimit())
                    .offset(request.getOffset());
            final List<Account> accounts = accountJPAQuery.fetch();
            final long total = accountRepository.count();
            final UsersResponse response = new UsersResponse(accounts.stream().map(accountDTOMapper::convertToDto).toList(), total);
            response.setPage(request.getPage());
            response.setLimit(request.getLimit());
            return response;
        }
    }
}
