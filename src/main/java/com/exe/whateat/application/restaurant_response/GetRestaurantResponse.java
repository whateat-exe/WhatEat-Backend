package com.exe.whateat.application.restaurant_response;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.restaurant_response.mapper.RestaurantResponseMapper;
import com.exe.whateat.application.restaurant_response.response.RestaurantResponseResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RestaurantRequestResponse;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRequestResponseRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetRestaurantResponse {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant_response",
            description = "APIs for request food."
    )
    public static final class GetRestaurantRequestResponseController extends AbstractController {

        private final GetResponseService service;

        @GetMapping("/restaurant-responses/{id}")
        @Operation(
                summary = "Get restaurant response API"
        )
        @ApiResponse(
                description = "Successful. Returns the request.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantResponseResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the responses.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRequest(@PathVariable Tsid id) {
            final RestaurantResponseResponse response = service.get(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class GetResponseService {

        private final RestaurantRequestResponseRepository restaurantRequestResponseRepository;
        private final RestaurantResponseMapper restaurantResponseMapper;

        public RestaurantResponseResponse get(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final RestaurantRequestResponse restaurantRequestResponse = restaurantRequestResponseRepository.findById(whatEatId)
                    .orElseThrow(() -> {
                        throw WhatEatException.builder()
                                .code(WhatEatErrorCode.WES_0001)
                                .reason("restaurantResponse", "Truyền Id bị sai")
                                .build();
                    });
            return restaurantResponseMapper.convertToDto(restaurantRequestResponse);
        }
    }
}
