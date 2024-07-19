package com.exe.whateat.application.restaurant_request;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.restaurant_request.mapper.RequestMapper;
import com.exe.whateat.application.restaurant_request.response.RequestResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RestaurantRequest;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRequestRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetRestaurantRequest {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant_request",
            description = "APIs for request food."
    )
    public static final class GetRestaurantRequestsController extends AbstractController {

        private final GetRequestService service;

        @GetMapping("/restaurant-requests/{id}")
        @Operation(
                summary = "Get restaurant request API"
        )
        @ApiResponse(
                description = "Successful. Returns the request.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RequestResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the requests.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRequest(@PathVariable Tsid id) {
            final RequestResponse response = service.get(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class GetRequestService {

        private final RestaurantRequestRepository restaurantRequestRepository;
        private final RequestMapper requestMapper;
        private final WhatEatSecurityHelper securityHelper;

        public RequestResponse get(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final RestaurantRequest restaurantRequest = restaurantRequestRepository.findById(whatEatId)
                    .orElseThrow(() -> {
                         throw WhatEatException.builder()
                                 .code(WhatEatErrorCode.WES_0001)
                                 .reason("restaurantRequest", "Truyền Id bị sai")
                                 .build();
                    });
            return requestMapper.convertToDto(restaurantRequest);
        }
    }
}
