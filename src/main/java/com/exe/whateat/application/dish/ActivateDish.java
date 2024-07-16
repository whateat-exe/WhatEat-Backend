package com.exe.whateat.application.dish;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.SubscriptionStatus;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActivateDish {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "dish",
            description = "APIs for dishes."
    )
    public static final class ActivateDishController extends AbstractController {

        private final ActivateDishService service;

        @PostMapping("/dishes/{id}/activate")
        @Operation(
                summary = "Activate dish API"
        )
        @ApiResponse(
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed activating the dish.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> activateDish(@PathVariable Tsid id) {
            service.activateDish(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class ActivateDishService {

        private final DishRepository dishRepository;
        private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
        private final WhatEatSecurityHelper securityHelper;

        public void activateDish(Tsid id) {
            final Account acc = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());

            final WhatEatId whatEatId = new WhatEatId(id);
            final Dish dish = dishRepository.findById(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0015)
                            .reason("dish", String.format("Món ăn với ID '%s' không tồn tại.", id))
                            .build());
            if (dish.getStatus() == ActiveStatus.ACTIVE) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0007)
                        .reason("status", "Món ăn đã được kích hoạt trước đó.")
                        .build();
            }

            final WhatEatId restaurantId = acc.getRestaurant().getId();
            Long numOfActivatedDishes = dishRepository.countByStatusAndRestaurantId(ActiveStatus.ACTIVE, restaurantId);
            Optional<RestaurantSubscriptionTracker> subscriptionTracker = restaurantSubscriptionTrackerRepository.findByRestaurantIdAndSubscriptionStatus(restaurantId, SubscriptionStatus.ACTIVE);

            if (subscriptionTracker.isEmpty()) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0021)
                        .reason("subscription", "Vui lòng đăng ký gói để kích hoạt món.")
                        .build();
            } else {
                RestaurantSubscriptionTracker tracker = subscriptionTracker.get();
                Integer maxDishes = switch (tracker.getSubscription().getType()) {
                    case SILVER -> 10;
                    case GOLD -> 30;
                    case DIAMOND -> 50;
                };
                if (numOfActivatedDishes >= maxDishes) {
                    throw WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0023)
                            .reason("subscription", "Bạn đã đạt giới hạn số món cho phép với gói hiện tại. Vui lòng vô hiệu hoá món khác để kích hoạt món hiện tại")
                            .build();
                }
            }

            dish.setStatus(ActiveStatus.ACTIVE);
            dishRepository.save(dish);
        }
    }
}
