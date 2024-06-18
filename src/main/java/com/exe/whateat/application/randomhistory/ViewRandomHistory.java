package com.exe.whateat.application.randomhistory;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.randomhistory.response.RandomHistoriesResponse;
import com.exe.whateat.application.randomhistory.response.RandomHistoryResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.random.QRandomHistory;
import com.exe.whateat.entity.random.RandomHistory;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ViewRandomHistory {

    public static final class ViewRandomHistoryRequest extends PaginationRequest {

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "random",
            description = "APIs for random history."
    )
    public static final class ViewRandomHistoryController extends AbstractController {

        private final ViewRandomHistoryService service;

        @GetMapping("/foods/random/history")
        @Operation(
                summary = "Get random history API. Returns the food from the ID. USER only."
        )
        @ApiResponse(
                description = "Successful. Returns the random history.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RandomHistoriesResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the random history.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRandomHistory(@ParameterObject ViewRandomHistoryRequest request) {
            final RandomHistoriesResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class ViewRandomHistoryService {

        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final WhatEatSecurityHelper securityHelper;
        private final WhatEatMapper<RandomHistory, RandomHistoryResponse> mapper;

        public RandomHistoriesResponse get(ViewRandomHistoryRequest request) {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            final QRandomHistory qRandomHistory = QRandomHistory.randomHistory;
            BlazeJPAQuery<RandomHistory> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory);
            final List<RandomHistory> randomHistories = query
                    .select(qRandomHistory)
                    .from(qRandomHistory)
                    .where(qRandomHistory.account.eq(account))
                    .orderBy(qRandomHistory.id.id.desc())
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .fetch();
            final long count = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                    .select(qRandomHistory)
                    .from(qRandomHistory)
                    .where(qRandomHistory.account.eq(account))
                    .fetchCount();
            final RandomHistoriesResponse response = new RandomHistoriesResponse(randomHistories.stream()
                    .map(mapper::convertToDto).toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }
    }
}
