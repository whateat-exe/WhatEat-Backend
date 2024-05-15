package com.exe.whateat.application.food;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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
public final class GetFood {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "food",
            description = "APIs for food."
    )
    public static final class GetFoodController extends AbstractController {

        private final GetFoodService service;

        @GetMapping("/foods/{id}")
        @Operation(
                summary = "Get food API. Returns the food from the ID."
        )
        @ApiResponse(
                description = "Successful. Returns the food. ADMIN & MANAGER will return any status, while others will return only ACTIVE one.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getFood(@PathVariable Tsid id) {
            final FoodResponse response = service.get(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class GetFoodService {

        private final FoodRepository foodRepository;
        private final WhatEatMapper<Food, FoodResponse> mapper;
        private final WhatEatSecurityHelper securityHelper;

        public FoodResponse get(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final Food food = foodRepository.findByIdWithParent(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0005)
                            .reason("food", String.format("Món ăn với ID '%s' không tồn tại.", id))
                            .build());
            if (securityHelper.currentAccountIsNotAdminOrManager() && food.getStatus() != ActiveStatus.ACTIVE) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEA_0002)
                        .reason("account", "Tài khoản hiện tại không đủ quyền.")
                        .build();
            }
            return mapper.convertToDto(food);
        }
    }
}
