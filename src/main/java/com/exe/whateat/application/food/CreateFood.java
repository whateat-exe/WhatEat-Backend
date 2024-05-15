package com.exe.whateat.application.food;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateFood {

    @Data
    @Builder
    public static final class CreateFoodRequest {

        @NotBlank(message = "Tên của món ăn bắt buộc phải có.")
        private String name;

        private Tsid parentFoodId;

        @NotBlank(message = "Ảnh của món ăn bắt buộc phải có.")
        private String image;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "food",
            description = "APIs for food."
    )
    public static final class CreateFoodController extends AbstractController {

        private final CreateFoodService service;

        @PostMapping("/foods")
        @Operation(
                summary = "Create food API. Returns the new information of food. ADMIN/MANAGER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food.",
                        content = @Content(schema = @Schema(implementation = CreateFoodRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns new information of the food.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createFood(@RequestBody @Valid CreateFoodRequest request) {
            final FoodResponse response = service.create(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreateFoodService {

        private final FoodRepository foodRepository;
        private final FirebaseImageService firebaseImageService;
        private final WhatEatMapper<Food, FoodResponse> mapper;

        @SuppressWarnings("Duplicates")
        public FoodResponse create(CreateFoodRequest request) {
            if (foodRepository.existsByNameIgnoreCase(request.getName())) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0004)
                        .reason("name", "Tên của món ăn đã tồn tại.")
                        .build();
            }
            final Tsid parentFoodId = request.getParentFoodId();
            if (request.getParentFoodId() != null && !foodRepository.existsById(new WhatEatId(request.getParentFoodId()))) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0005)
                        .reason("parentFood", String.format("Món ăn với ID '%s' không tồn tại.", request.getParentFoodId()))
                        .build();
            }
            Food food = Food.builder()
                    .id(WhatEatId.generate())
                    .name(request.getName())
                    .parentFood(parentFoodId != null
                            ? foodRepository.getReferenceById(new WhatEatId(parentFoodId))
                            : null)
                    .status(ActiveStatus.ACTIVE)
                    .build();
            FirebaseImageResponse firebaseImageResponse = null;
            try {
                firebaseImageResponse = firebaseImageService.uploadBase64Image(request.getImage());
                food.setImage(firebaseImageResponse.url());
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
    }
}
