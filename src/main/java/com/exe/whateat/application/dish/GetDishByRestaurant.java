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
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QDish;
import com.exe.whateat.entity.random.QRating;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetDishByRestaurant {

    @Data
    private static final class GetDishesByRestaurantRequest extends PaginationRequest {

        private String name;
        private DishFilter asc;
        private DishFilter desc;
        private ActiveStatus status;

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "dish",
            description = "get dishes by restaurant"
    )
    public static class GetDishesController extends AbstractController {

        private GetDishesByRestaurantService getDishesByRestaurant;

        @Operation(
                summary = "Get dishes API of a restaurant. Returns the list of dishes paginated"
        )
        @ApiResponse(
                description = "Successful. Returns list of the dishes.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = DishesResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the foods.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/dishes/restaurants/{id}")
        public ResponseEntity<Object> getDishes(@PathVariable Tsid id, @ParameterObject GetDishesByRestaurantRequest getDishesRequest) {
            var response = getDishesByRestaurant.getDishesByRestaurant(getDishesRequest, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetDishesByRestaurantService {

        private DishMapper dishMapper;
        private EntityManager entityManager;
        private RestaurantRepository restaurantRepository;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        public DishesResponse getDishesByRestaurant(GetDishesByRestaurantRequest getDishesByRestaurantRequest, Tsid tsid) {
            final QDish qDish = QDish.dish;
            final QRating qRating = QRating.rating;
            final WhatEatId whatEatRestaurantId = WhatEatId.builder().id(tsid).build();

            BooleanExpression predicates = qDish.isNotNull();

            if (!restaurantRepository.existsById(whatEatRestaurantId)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0002)
                        .reason("food", String.format("Nhà hàng với ID '%s' không tồn tại.", tsid))
                        .build();
            }

            predicates = predicates.and(qDish.restaurant.id.eq(whatEatRestaurantId));

            if (StringUtils.isNotBlank(getDishesByRestaurantRequest.getName())) {
                predicates = predicates.and(qDish.name.containsIgnoreCase(getDishesByRestaurantRequest.getName()));
            }

            if (getDishesByRestaurantRequest.getStatus() != null) {
                predicates = predicates.and(qDish.status.eq(getDishesByRestaurantRequest.getStatus()));
            }

            if (getDishesByRestaurantRequest.getAsc() != null && getDishesByRestaurantRequest.getDesc() != null) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0000)
                        .reason("filter", "Vui lòng chỉ sử dụng 1 filter: asc hoặc desc.")
                        .build();
            }

            JPAQuery<Dish> query = new JPAQuery<>(entityManager);
            List<Dish> dishes;

            if (DishFilter.REVIEW.equals(getDishesByRestaurantRequest.getAsc()) || DishFilter.REVIEW.equals(getDishesByRestaurantRequest.getDesc())) {
                query.select(qDish)
                        .from(qDish)
                        .leftJoin(qDish.ratings, qRating)
                        .having(predicates)
                        .groupBy(qDish.id);

                if (DishFilter.REVIEW.equals(getDishesByRestaurantRequest.getAsc())) {
                    query.orderBy(qRating.stars.avg().asc());
                } else {
                    query.orderBy(qRating.stars.avg().desc());
                }
            } else if (DishFilter.PRICE.equals(getDishesByRestaurantRequest.getAsc()) || DishFilter.PRICE.equals(getDishesByRestaurantRequest.getDesc())) {
                query.select(qDish)
                        .from(qDish)
                        .where(predicates);

                if (DishFilter.PRICE.equals(getDishesByRestaurantRequest.getAsc())) {
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
                    .limit(getDishesByRestaurantRequest.getLimit())
                    .offset(getDishesByRestaurantRequest.getOffset())
                    .fetch();

            final long count = new BlazeJPAQuery<FoodTag>(entityManager, criteriaBuilderFactory)
                    .select(qDish)
                    .from(qDish)
                    .where(predicates)
                    .fetchCount();

            final DishesResponse response = new DishesResponse(dishes.stream().map(dishMapper::convertToDto).toList(), count);
            response.setLimit(getDishesByRestaurantRequest.getLimit());
            response.setPage(getDishesByRestaurantRequest.getPage());
            return response;
        }
    }
}
