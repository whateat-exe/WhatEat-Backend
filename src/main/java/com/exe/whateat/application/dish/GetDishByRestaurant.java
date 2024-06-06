package com.exe.whateat.application.dish;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.dish.mapper.DishMapper;
import com.exe.whateat.application.dish.response.DishesResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QDish;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import io.github.x4ala1c.tsid.Tsid;
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
public final class GetDishByRestaurant {

    @Data
    private static final class GetDishesByRestaurantRequest extends PaginationRequest {

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
        public ResponseEntity<Object> getDishes(@PathVariable Tsid id, @Valid GetDishesByRestaurantRequest getDishesRequest) {
            var response = getDishesByRestaurant.getDishesByRestaurant(getDishesRequest, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetDishesByRestaurantService {

        private DishMapper dishMapper;
        private EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        public DishesResponse getDishesByRestaurant(GetDishesByRestaurantRequest getDishesByRestaurantRequest, Tsid tsid) {
            final WhatEatId whatEatRestaurantId = WhatEatId.builder().id(tsid).build();
            final QDish qDish = QDish.dish;
            BlazeJPAQuery<Dish> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory);
            final List<Dish> dishes = query
                    .select(qDish)
                    .from(qDish)
                    .leftJoin(qDish.food).fetchJoin()
                    .leftJoin(qDish.restaurant).fetchJoin()
                    .where(qDish.restaurant.id.eq(whatEatRestaurantId))
                    .limit(getDishesByRestaurantRequest.getLimit())
                    .offset(getDishesByRestaurantRequest.getOffset())
                    .fetch();
            final long count = new BlazeJPAQuery<FoodTag>(entityManager, criteriaBuilderFactory)
                    .select(qDish)
                    .from(qDish)
                    .fetchCount();
            final DishesResponse response = new DishesResponse(dishes.stream().map(dishMapper::convertToDto).toList(), count);
            response.setLimit(getDishesByRestaurantRequest.getLimit());
            response.setPage(getDishesByRestaurantRequest.getPage());
            return response;
        }
    }
}
