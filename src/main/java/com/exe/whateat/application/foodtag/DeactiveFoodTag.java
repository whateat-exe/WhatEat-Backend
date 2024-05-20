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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeactiveFoodTag {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtag",
            description = "Deactive a food tag"
    )
    public static final class DeactiveFoodTagController extends AbstractController {

        private DeactiveFoodTagService deactiveFoodTagService;

        @DeleteMapping("/foodtags/{id}")
        @Operation(
                summary = "Deactivate food tag API. ADMIN/MANAGER only."
        )
        @ApiResponse(
                description = "Successful. Returns the food tag.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed getting of the food tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> deactiveFoodTag(@PathVariable Tsid id) {
            deactiveFoodTagService.deactiveFoodTag(id);
            return ResponseEntity.ok("Deactive foodtag successfully");
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class DeactiveFoodTagService {

        private FoodTagRepository foodTagRepository;

        public void deactiveFoodTag(Tsid id) {
            var foodTag = foodTagRepository.findById(WhatEatId.builder().id(id).build());
            if (!foodTag.isPresent())
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("lỗi gửi id", "gửi id sai hoặc không đúng định dạng")
                        .build();
            foodTag.get().setStatus(ActiveStatus.INACTIVE);
        }
    }
}
