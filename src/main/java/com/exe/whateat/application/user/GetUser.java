package com.exe.whateat.application.user;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.food.response.FoodsResponse;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.application.user.response.UserResponse;
import com.exe.whateat.application.user.response.UsersResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.QAccount;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.food.QFood;
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
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetUser {

    public static final class GetUserRequest extends PaginationRequest {

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "user",
            description = "APIs for get users"
    )
    public static class GetUserController extends AbstractController {

        private final GetUserService getUserService;

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
        public ResponseEntity<Object> getAllAccount(@Valid GetUserRequest getUserRequest) {
            final UsersResponse response = getUserService.getAllUser(getUserRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetUserService {

        private final AccountRepository accountRepository;
        private final AccountDTOMapper accountDTOMapper;
        private final EntityManager entityManager;

        public UsersResponse getAllUser(GetUserRequest getUserRequest) {

            final QAccount qAccount = QAccount.account;
            final JPAQuery<Account> accountJPAQuery = new JPAQuery<>(entityManager)
                    .select(qAccount)
                    .from(qAccount)
                    .limit(getUserRequest.getLimit())
                    .offset(getUserRequest.getOffset());
            final List<Account> accounts = accountJPAQuery.fetch();
            final long total = accountRepository.count();
            final UsersResponse response = new UsersResponse(accounts.stream().map(accountDTOMapper::convertToDto).toList(), total);
            response.setPage(getUserRequest.getPage());
            response.setLimit(getUserRequest.getLimit());
            return response;
        }
    }
}
