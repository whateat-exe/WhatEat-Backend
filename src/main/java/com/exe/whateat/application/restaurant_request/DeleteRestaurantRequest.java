package com.exe.whateat.application.restaurant_request;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRequestRepository;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteRestaurantRequest {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant_request",
            description = "APIs for restaurant request."
    )
    public static final class CreateRestaurantRequestDishController extends AbstractController {

        private DeleteRestaurantRequestService service;

        @DeleteMapping("/restaurant-requests/{id}")
        @Operation(
                summary = "Delete restaurant request API",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "No content"
                )
        )
        @ApiResponse(
                description = "Failed delete of the restaurant request.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> delete(@PathVariable Tsid id) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class DeleteRestaurantRequestService {

        private final RestaurantRequestRepository restaurantRequestRepository;

        public void delete(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            restaurantRequestRepository.deleteById(whatEatId);
        }
    }
}
