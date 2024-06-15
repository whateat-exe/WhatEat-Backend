package com.exe.whateat.application.dish;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
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

        public void activateDish(Tsid id) {
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
            dish.setStatus(ActiveStatus.ACTIVE);
            dishRepository.save(dish);
        }
    }
}
