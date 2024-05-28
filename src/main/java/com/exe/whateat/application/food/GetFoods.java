package com.exe.whateat.application.food;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.food.response.FoodsResponse;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.food.QFood;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetFoods {

    @Getter
    @Setter
    public static final class GetFoodsRequest extends PaginationRequest {

        private String name;
        private ActiveStatus status;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "food",
            description = "APIs for food."
    )
    public static final class GetFoodsController extends AbstractController {

        private final GetFoodsService service;

        @GetMapping("/foods")
        @Operation(
                summary = "Get foods API. Returns the list of foods paginated. ADMIN & MANAGER will return all, while others will return only ACTIVE foods."
        )
        @ApiResponse(
                description = "Successful. Returns list of the foods.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the foods.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getFoods(@ParameterObject GetFoodsRequest request) {
            final FoodsResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    public static class GetFoodsService {

        private final FoodRepository foodRepository;
        private final WhatEatMapper<Food, FoodResponse> mapper;
        private final WhatEatSecurityHelper securityHelper;

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        public GetFoodsService(FoodRepository foodRepository, WhatEatMapper<Food, FoodResponse> mapper,
                               WhatEatSecurityHelper securityHelper) {
            this.foodRepository = foodRepository;
            this.mapper = mapper;
            this.securityHelper = securityHelper;
        }

        public FoodsResponse get(GetFoodsRequest request) {
            final QFood qFood = QFood.food;
            BooleanExpression predicates = qFood.isNotNull();
            if (StringUtils.isNotBlank(request.getName())) {
                predicates = predicates.and(qFood.name.containsIgnoreCase(request.getName()));
            }
            if (request.getStatus() != null) {
                predicates = predicates.and(qFood.status.eq(request.getStatus()));
            }
            final JPAQuery<Food> foodQuery = new JPAQuery<>(entityManager)
                    .select(qFood)
                    .from(qFood)
                    .where(predicates)
                    .limit(request.getLimit())
                    .offset(request.getOffset());
            if (securityHelper.currentAccountIsNotAdminOrManager()) {
                foodQuery.where(qFood.status.eq(ActiveStatus.ACTIVE));
            }
            final List<Food> foods = foodQuery.fetch();
            final long total = foodRepository.count();
            final FoodsResponse response = new FoodsResponse(foods.stream().map(mapper::convertToDto).toList(), total);
            response.setPage(request.getPage());
            response.setLimit(request.getLimit());
            return response;
        }
    }
}
