package com.exe.whateat.application.food;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateFood {

    @Data
    @Builder
    public static final class UpdateFoodRequest {

        private String name;
        private String image;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "food",
            description = "APIs for food."
    )
    public static final class UpdateFoodController extends AbstractController {

        private final UpdateFoodService service;

        @PatchMapping("/foods/{id}")
        @Operation(
                summary = "Update food API. Returns the new information of food. ADMIN/MANAGER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food.",
                        content = @Content(schema = @Schema(implementation = UpdateFoodRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful update. Returns new information of the food.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> updateFood(@PathVariable Tsid id, @RequestBody UpdateFoodRequest request) {
            final FoodResponse response = service.update(id, request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class UpdateFoodService {

        private final FoodRepository foodRepository;
        private final FirebaseImageService firebaseImageService;
        private final WhatEatMapper<Food, FoodResponse> mapper;

        @SuppressWarnings("Duplicates")
        public FoodResponse update(Tsid id, UpdateFoodRequest request) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final Food food = foodRepository.findByIdWithParent(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEB_0005)
                            .reason("food", String.format("Món ăn với ID '%s' không tồn tại.", id))
                            .build());
            if (StringUtils.isNotBlank(request.getName())) {
                verifyAndSetName(request, food);
            }
            FirebaseImageResponse firebaseImageResponse = null;
            try {
                if (StringUtils.isNotBlank(request.getImage())) {
                    firebaseImageResponse = firebaseImageService.uploadBase64Image(request.getImage());
                    food.setImage(firebaseImageResponse.url());
                }
                return mapper.convertToDto(foodRepository.saveAndFlush(food));
            } catch (Exception e) {
                // Image is created. Time to delete!
                if (firebaseImageResponse != null) {
                    firebaseImageService.deleteImage(firebaseImageResponse.id(), FirebaseImageService.DeleteType.ID);
                }
                if (e instanceof WhatEatException whatEatException) {
                    throw whatEatException;
                }
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("food", "Lỗi trong việc tạo món ăn. Vui lòng thử lại hoặc báo admin.")
                        .build();
            }
        }

        private void verifyAndSetName(UpdateFoodRequest request, Food food) {
            if (foodRepository.existsByNameIgnoreCase(request.getName())) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0004)
                        .reason("name", "Tên của món ăn đã tồn tại.")
                        .build();
            }
            food.setName(request.getName());
        }
    }
}
