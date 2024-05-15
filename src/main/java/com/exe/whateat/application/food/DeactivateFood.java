package com.exe.whateat.application.food;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
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
public final class DeactivateFood {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "food",
            description = "APIs for food."
    )
    public static final class DeactivateFoodController extends AbstractController {

        private final DeactivateFoodService service;

        @DeleteMapping("/foods/{id}")
        @Operation(
                summary = "Deactivate food API. ADMIN/MANAGER only."
        )
        @ApiResponse(
                description = "Successful. Returns the food.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed getting of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> deleteFood(@PathVariable Tsid id) {
            service.deactivate(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class DeactivateFoodService {

        private final FoodRepository foodRepository;

        public void deactivate(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final Food food = foodRepository.findByIdWithParent(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0005)
                            .reason("food", String.format("Món ăn với ID '%s' không tồn tại.", id))
                            .build());
            if (food.getStatus() == ActiveStatus.INACTIVE) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0007)
                        .reason("status", "Món ăn đã được vô hiệu hóa trước đó.")
                        .build();
            }
            food.setStatus(ActiveStatus.INACTIVE);
            foodRepository.save(food);
        }
    }
}
