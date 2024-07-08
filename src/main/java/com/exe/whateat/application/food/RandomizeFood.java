package com.exe.whateat.application.food;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.randomhistory.RandomService;
import com.exe.whateat.application.randomhistory.response.RandomResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomizeFood {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "random",
            description = "APIs for randomization."
    )
    public static final class RandomizeFoodController extends AbstractController {

        private final RandomizeFoodService service;

        @GetMapping("/foods/random")
        @Operation(
                summary = "Random food API. Returns the food randomized."
        )
        @ApiResponse(
                description = "Successful. Returns the food. Only for USER.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> randomFood() {
            final FoodResponse response = service.random();
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional
    @RequiredArgsConstructor
    public static class RandomizeFoodService {

        private static final Random RANDOM = new SecureRandom();

        private final FoodRepository foodRepository;
        private final WhatEatMapper<Food, FoodResponse> mapper;
        private final RandomService randomService;
        private final WhatEatSecurityHelper securityHelper;
        private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;

        public FoodResponse random() {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            final RandomResponse randomResponse = randomService.checkIfAllowedToRandomize(account);
            if (randomResponse.notAllowedToRandomize()) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0013)
                        .reason("cooldown", "Số lượng ngẫu nhiên đã đạt tới giới hạn.")
                        .build();
            }
            final List<Food> foods;
            if (userSubscriptionTrackerRepository.userIsUnderActiveSubscription(account.getId())) {
                foods = foodRepository.subscribedRandom(account.getId().asTsid().asLong());
            } else {
                foods = foodRepository.freeRandom();
            }
            if (foods.isEmpty()) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0013)
                        .reason("food", "Bạn quá khó tính. Giảm filter xuống đê.")
                        .build();
            }
            final int position = RANDOM.nextInt(foods.size());
            final Food food = foods.get(position);
            randomService.saveRandomHistory(account, food, randomResponse.shouldBeReset());
            return mapper.convertToDto(foods.get(position));
        }
    }
}
