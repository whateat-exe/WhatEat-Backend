package com.exe.whateat.application.restaurant_request;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.food.response.FoodsResponse;
import com.exe.whateat.application.restaurant_request.mapper.RequestMapper;
import com.exe.whateat.application.restaurant_request.response.RequestResponse;
import com.exe.whateat.application.restaurant_request.response.RequestResponses;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.food.QFood;
import com.exe.whateat.entity.request.QRestaurantRequest;
import com.exe.whateat.entity.request.RestaurantRequest;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRequestRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
public final class GetRestaurantRequests {

    @Getter
    @Setter
    public static final class GetRequestsRequest extends PaginationRequest {
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant_request",
            description = "APIs for request food."
    )
    public static final class GetRestaurantRequestsController extends AbstractController {

        private final GetRequestsService service;

        @GetMapping("/restaurant-requests")
        @Operation(
                summary = "Get restaurant request API"
        )
        @ApiResponse(
                description = "Successful. Returns list of the requests.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RequestResponses.class))
        )
        @ApiResponse(
                description = "Failed getting of the requests.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRequests(@ParameterObject GetRequestsRequest getRequestsRequest) {
            final RequestResponses response = service.get(getRequestsRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional
    public static class GetRequestsService {

        private RestaurantRequestRepository restaurantRequestRepository;
        private RequestMapper requestMapper;
        @PersistenceContext
        private EntityManager entityManager;

        public RequestResponses get(GetRequestsRequest request) {
            final QRestaurantRequest qRestaurantRequest = QRestaurantRequest.restaurantRequest;
            final JPAQuery<RestaurantRequest> restaurantRequestJPAQuery = new JPAQuery<>(entityManager)
                    .select(qRestaurantRequest)
                    .from(qRestaurantRequest)
                    .limit(request.getLimit())
                    .offset(request.getOffset());
            final List<RestaurantRequest> requests = restaurantRequestJPAQuery.fetch();
            final long total = restaurantRequestRepository.count();
            final RequestResponses response = new RequestResponses(requests.stream().map(requestMapper::convertToDto).toList(), total);
            response.setPage(request.getPage());
            response.setLimit(request.getLimit());
            return response;
        }
    }
}
