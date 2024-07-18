package com.exe.whateat.application.dish;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.dish.mapper.DishMapper;
import com.exe.whateat.application.dish.request.DishFilter;
import com.exe.whateat.application.dish.response.DishesResponse;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.food.QDish;
import com.exe.whateat.entity.random.QRating;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetDishesByFood {

    @Getter
    @Setter
    @NoArgsConstructor
    public static final class GetDishesByFoodRequest extends PaginationRequest {

        private String name;
        private DishFilter asc;
        private DishFilter desc;
        private ActiveStatus status;

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "dish"
    )
    public static class GetDishesController extends AbstractController {

        private GetDishesByFoodService service;

        @Operation(
                summary = "Get dishes by food API. Returns the list of dishes paginated"
        )
        @ApiResponse(
                description = "Successful. Returns list of the dishes.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = DishesResponse.class))
        )
        @ApiResponse(
                description = "Failed getting dishes.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/dishes/foods/{id}")
        public ResponseEntity<Object> getDishes(@PathVariable Tsid id, @ParameterObject GetDishesByFoodRequest request) {
            var response = service.get(request, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional
    public static class GetDishesByFoodService {

        private DishMapper dishMapper;
        private EntityManager entityManager;
        private FoodRepository foodRepository;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        @SuppressWarnings("Duplicates")
        public DishesResponse get(GetDishesByFoodRequest request, Tsid tsid) {
            final QDish qDish = QDish.dish;
            final QRating qRating = QRating.rating;
            BooleanExpression predicates = qDish.isNotNull();

            if (StringUtils.isNotBlank(request.getName())) {
                predicates = predicates.and(qDish.name.containsIgnoreCase(request.getName()));
            }

            if (request.getStatus() != null) {
                predicates = predicates.and(qDish.status.eq(request.getStatus()));
            }

            if (request.getAsc() != null && request.getDesc() != null) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0000)
                        .reason("filter", "Vui lòng chỉ sử dụng 1 filter: asc hoặc desc.")
                        .build();
            }

            final WhatEatId WhatEatFoodId = WhatEatId.builder().id(tsid).build();

            if (!foodRepository.existsById(WhatEatFoodId)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0005)
                        .reason("food", String.format("Món ăn với ID '%s' không tồn tại.", tsid))
                        .build();
            }

            predicates = predicates.and(qDish.food.id.eq(WhatEatFoodId));

            JPAQuery<Dish> query = new JPAQuery<>(entityManager);
            List<Dish> dishes;

            if (DishFilter.REVIEW.equals(request.getAsc()) || DishFilter.REVIEW.equals(request.getDesc())) {
                query.select(qDish)
                        .from(qDish)
                        .leftJoin(qDish.ratings, qRating)
                        .having(predicates)
                        .groupBy(qDish.id);

                if (DishFilter.REVIEW.equals(request.getAsc())) {
                    query.orderBy(qRating.stars.avg().coalesce(0.0).asc());
                } else {
                    query.orderBy(qRating.stars.avg().coalesce(0.0).desc());
                }
            } else if (DishFilter.PRICE.equals(request.getAsc()) || DishFilter.PRICE.equals(request.getDesc())) {
                query.select(qDish)
                        .from(qDish)
                        .where(predicates);

                if (DishFilter.PRICE.equals(request.getAsc())) {
                    query.orderBy(qDish.price.amount.asc());
                } else {
                    query.orderBy(qDish.price.amount.desc());
                }
            } else {
                query.select(qDish)
                        .from(qDish)
                        .where(predicates);
            }

            dishes = query
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .fetch();

            final long count = new BlazeJPAQuery<Dish>(entityManager, criteriaBuilderFactory)
                    .select(qDish)
                    .from(qDish)
                    .where(predicates)
                    .fetchCount();

            final DishesResponse response = new DishesResponse(dishes.stream().map(dishMapper::convertToDto).toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }
    }

}
