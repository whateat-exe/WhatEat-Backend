package com.exe.whateat.application.restaurant;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetCurrentRestaurant {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant",
            description = "APIs for restaurant accounts."
    )
    public static final class GetCurrentRestaurantController extends AbstractController {

        private final GetCurrentRestaurantService service;

        @GetMapping("/restaurants/current")
        @Operation(
                summary = "Get restaurant through current logged in account. Returns the information of the restaurant. Only for RESTAURANT."
        )
        @ApiResponse(
                description = "Successfully found.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the current restaurant.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getRestaurant() {
            final RestaurantResponse response = service.get();
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class GetCurrentRestaurantService {

        private final WhatEatSecurityHelper securityHelper;
        private final WhatEatMapper<Restaurant, RestaurantResponse> mapper;

        public RestaurantResponse get() {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0002)
                            .reason("account", "Unknown account.")
                            .build());
            return mapper.convertToDto(account.getRestaurant());
        }
    }
}
