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
public final class DeactivateFoodTag {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtags",
            description = "APIs for food tags."
    )
    public static final class DeactivateFoodTagController extends AbstractController {

        private DeactivateFoodTagService deactivateFoodTagService;

        @PostMapping("/foodtags/{id}/deactivate")
        @Operation(
                summary = "Deactivates food tag API. ADMIN/MANAGER only."
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
        public ResponseEntity<Object> deactiveFoodTag(@PathVariable Tsid id) {
            deactivateFoodTagService.deactivate(id);
            return ResponseEntity.ok("Deactive foodtag successfully");
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class DeactivateFoodTagService {

        private FoodTagRepository foodTagRepository;

        public void deactivate(Tsid id) {
            var foodTag = foodTagRepository.findById(new WhatEatId(id))
                    .orElseThrow(() -> WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0010)
                            .reason("id", "Món ăn với nhãn trên không tồn tại.")
                            .build());
            if (foodTag.getStatus() == ActiveStatus.INACTIVE) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0012)
                        .reason("status", "Món ăn với nhãn trên đã bị vô hiệu hóa trước đó.")
                        .build();
            }
            foodTag.setStatus(ActiveStatus.INACTIVE);
            foodTagRepository.save(foodTag);
        }
    }
}
