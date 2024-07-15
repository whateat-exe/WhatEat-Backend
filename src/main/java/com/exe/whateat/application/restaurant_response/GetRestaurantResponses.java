package com.exe.whateat.application.restaurant_response;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.restaurant_response.mapper.RestaurantResponseMapper;
import com.exe.whateat.application.restaurant_response.response.RestaurantResponsesResponse;
import com.exe.whateat.entity.request.QRestaurantRequestResponse;
import com.exe.whateat.entity.request.RestaurantRequestResponse;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRequestResponseRepository;
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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetRestaurantResponses {

    @Getter
    @Setter
    public static final class GetRestaurantResponsesRequest extends PaginationRequest {
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant_response",
            description = "APIs for restaurant response."
    )
    public static final class GetRestaurantResponseDishController extends AbstractController {

        private final GetRestaurantResponsesService service;

        @GetMapping("/restaurant-responses")
        @Operation(
                summary = "Get restaurant request API"
        )
        @ApiResponse(
                description = "Successful. Returns list of the response.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantResponsesResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the requests.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRequests(@ParameterObject GetRestaurantResponsesRequest request) {
            final RestaurantResponsesResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional
    public static class GetRestaurantResponsesService {

        private RestaurantRequestResponseRepository restaurantRequestResponseRepository;
        private RestaurantResponseMapper restaurantResponseMapper;
        @PersistenceContext
        private EntityManager entityManager;

        public RestaurantResponsesResponse get(GetRestaurantResponsesRequest request) {
            final QRestaurantRequestResponse qRestaurantRequestResponse = QRestaurantRequestResponse.restaurantRequestResponse;
            final JPAQuery<RestaurantRequestResponse> restaurantRequestJPAQuery = new JPAQuery<>(entityManager)
                    .select(qRestaurantRequestResponse)
                    .from(qRestaurantRequestResponse)
                    .limit(request.getLimit())
                    .offset(request.getOffset());
            final List<RestaurantRequestResponse> requests = restaurantRequestJPAQuery.fetch();
            final long total = restaurantRequestResponseRepository.count();
            final RestaurantResponsesResponse response = new RestaurantResponsesResponse(requests.stream().map(restaurantResponseMapper::convertToDto).toList(), total);
            response.setPage(request.getPage());
            response.setLimit(request.getLimit());
            return response;
        }
    }
}
