package com.exe.whateat.application.dish;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.dish.response.CheckIfCanCreateDishResponse;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.SubscriptionStatus;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CheckIfCanCreateDish {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "dish"
    )
    public static final class CheckIfCanCreateDishController extends AbstractController {

        private final CheckIfCanCreateDishService service;

        @GetMapping("/dishes/check-create")
        @Operation(
                summary = "Check If Can Create Dish API"
        )
        @ApiResponse(
                description = "Successful.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = CheckIfCanCreateDishResponse.class))
        )
        @ApiResponse(
                description = "Failed.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> check() {
            final CheckIfCanCreateDishResponse response = service.check();
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CheckIfCanCreateDishService {

        private final DishRepository dishRepository;
        private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
        private final WhatEatSecurityHelper securityHelper;

        public CheckIfCanCreateDishResponse check() {
            final Account acc = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());

            final WhatEatId restaurantId = acc.getRestaurant().getId();

            Long numOfDishes = dishRepository.countByRestaurantId(acc.getRestaurant().getId());
            Optional<RestaurantSubscriptionTracker> subscriptionTracker = restaurantSubscriptionTrackerRepository.findByRestaurantIdAndSubscriptionStatus(restaurantId, SubscriptionStatus.ACTIVE);

            if (subscriptionTracker.isEmpty()) {
                return CheckIfCanCreateDishResponse.builder()
                        .canCreateDish(false)
                        .message("Vui lòng đăng ký gói để tạo món.")
                        .build();
            } else {
                RestaurantSubscriptionTracker tracker = subscriptionTracker.get();
                Integer maxDishes = switch (tracker.getSubscription().getType()) {
                    case SILVER -> 10;
                    case GOLD -> 30;
                    case DIAMOND -> 50;
                };
                if (numOfDishes >= maxDishes) {
                    return CheckIfCanCreateDishResponse.builder()
                            .canCreateDish(false)
                            .message("Bạn đã đạt giới hạn số món cho phép với gói hiện tại.")
                            .build();
                }
            }
            return CheckIfCanCreateDishResponse.builder()
                    .canCreateDish(true)
                    .message("Bạn có thể tạo món")
                    .build();

        }
    }

}
