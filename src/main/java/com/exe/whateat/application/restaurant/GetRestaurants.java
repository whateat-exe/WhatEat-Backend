package com.exe.whateat.application.restaurant;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.application.restaurant.response.RestaurantsResponse;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.restaurant.QRestaurant;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
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
public final class GetRestaurants {

    @Getter
    @Setter
    public static final class GetRestaurantsRequest extends PaginationRequest {

        private String name;
        private ActiveStatus status;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant",
            description = "APIs for restaurant accounts."
    )
    public static final class GetRestaurantsController extends AbstractController {

        private final GetRestaurantsService service;

        @GetMapping("/restaurants")
        @Operation(
                summary = "Get restaurants API. Returns the list of the restaurants paginated. Only for ADMIN and MANAGER."
        )
        @ApiResponse(
                description = "Successfully found.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantsResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the restaurants.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRestaurants(@ParameterObject GetRestaurantsRequest request) {
            final RestaurantsResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    public static final class GetRestaurantsService {

        private final RestaurantRepository restaurantRepository;
        private final WhatEatMapper<Restaurant, RestaurantResponse> mapper;

        @PersistenceContext
        private EntityManager entityManager;

        @Autowired
        public GetRestaurantsService(RestaurantRepository restaurantRepository,
                                     WhatEatMapper<Restaurant, RestaurantResponse> mapper) {
            this.restaurantRepository = restaurantRepository;
            this.mapper = mapper;
        }

        public RestaurantsResponse get(GetRestaurantsRequest request) {
            final QRestaurant qRestaurant = QRestaurant.restaurant;
            BooleanExpression predicates = qRestaurant.isNotNull();
            if (StringUtils.isNotBlank(request.getName())) {
                predicates = predicates.and(qRestaurant.name.containsIgnoreCase(request.getName()));
            }
            if (request.getStatus() != null) {
                predicates = predicates.and(qRestaurant.account.status.eq(request.getStatus()));
            }
            final JPAQuery<Restaurant> restaurantQuery = new JPAQuery<>(entityManager)
                    .select(qRestaurant)
                    .from(qRestaurant)
                    .where(predicates)
                    .limit(request.getLimit())
                    .offset(request.getOffset());
            final List<Restaurant> restaurants = restaurantQuery.fetch();
            final long total = restaurantRepository.count();
            final RestaurantsResponse response = new RestaurantsResponse(restaurants.stream().map(mapper::convertToDto).toList(), total);
            response.setPage(request.getPage());
            response.setLimit(request.getLimit());
            return response;
        }
    }
}
