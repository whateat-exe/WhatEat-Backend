package com.exe.whateat.application.restaurant;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetRestaurant {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant",
            description = "APIs for restaurant accounts."
    )
    public static final class GetRestaurantController extends AbstractController {

        private final GetRestaurantService service;

        @GetMapping("/restaurants/{id}")
        @Operation(
                summary = "Get restaurant through its ID API. Returns the information of the restaurant. Only for ADMIN."
        )
        @ApiResponse(
                description = "Successfully found.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the restaurant.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRestaurant(@PathVariable(name = "id") Tsid id) {
            final RestaurantResponse response = service.get(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class GetRestaurantService {

        private final RestaurantRepository restaurantRepository;
        private final WhatEatMapper<Restaurant, RestaurantResponse> mapper;

        @SuppressWarnings("Duplicates")
        public RestaurantResponse get(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final Restaurant restaurant = restaurantRepository.findById(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0002)
                            .reason("restaurant_id", "Unknown restaurant ID.")
                            .build());
            return mapper.convertToDto(restaurant);
        }
    }
}
