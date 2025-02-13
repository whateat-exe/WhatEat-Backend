package com.exe.whateat.application.restaurant;

import com.exe.whateat.application.account.verification.AccountVerificationService;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.ActiveStatus;
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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeactivateRestaurant {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant",
            description = "APIs for restaurant accounts."
    )
    public static final class DeactivateRestaurantController extends AbstractController {

        private final DeactivateRestaurantService service;

        @PostMapping("/restaurants/{id}/deactivate")
        @Operation(
                summary = "Update restaurant API. Returns the new information of restaurant."
        )
        @ApiResponse(
                description = "Successful deactivation.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed deactivation of the restaurant.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> deactivateRestaurant(@PathVariable(name = "id") Tsid id) {
            service.deactivate(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class DeactivateRestaurantService {

        private final RestaurantRepository restaurantRepository;
        private final AccountVerificationService accountVerificationService;

        public void deactivate(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final Restaurant restaurant = restaurantRepository.findById(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0002)
                            .reason("restaurantId", String.format("Tài khoản nhà hàng với ID '%s' không tồn tại.", whatEatId))
                            .build());
            if (restaurant.getAccount().getStatus() == ActiveStatus.INACTIVE) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0003)
                        .reason("restaurant", "Tài khoản đã bị vô hiệu hóa trước đó.")
                        .build();
            }
            restaurant.getAccount().setStatus(ActiveStatus.INACTIVE);
            restaurantRepository.save(restaurant);
            accountVerificationService.sendDeactivatingAccountEmail(restaurant.getAccount());
        }
    }
}
