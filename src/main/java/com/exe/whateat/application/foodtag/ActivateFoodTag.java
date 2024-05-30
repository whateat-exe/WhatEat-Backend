package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodTagRepository;
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
public final class ActivateFoodTag {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtags",
            description = "APIs for food tags."
    )
    public static final class ActivateFoodTagController extends AbstractController {

        private final ActivateFoodTagService service;

        @PostMapping("/foodtags/{id}/activate")
        @Operation(
                summary = "Activates food tag API. ADMIN/MANAGER only."
        )
        @ApiResponse(
                description = "Successful. No content is returned.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed getting of the food tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> activateFoodTag(@PathVariable Tsid id) {
            service.activate(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class ActivateFoodTagService {

        private final FoodTagRepository foodTagRepository;

        public void activate(Tsid id) {
            var foodTag = foodTagRepository.findById(new WhatEatId(id))
                    .orElseThrow(() -> WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0010)
                            .reason("id", "Món ăn với nhãn trên không tồn tại.")
                            .build());
            if (foodTag.getStatus() == ActiveStatus.ACTIVE) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0012)
                        .reason("status", "Món ăn với nhãn trên đã được kích hoạt trước đó.")
                        .build();
            }
            foodTag.setStatus(ActiveStatus.ACTIVE);
            foodTagRepository.save(foodTag);
        }
    }
}
