package com.exe.whateat.application.dish;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.dish.mapper.DishMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.entity.common.Money;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
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
public final class UpdateDish {

    @Data
    @Builder
    public static final class UpdateDishRequest {

        private String name;
        private String image;
        private String description;
        private Money price;
        private Tsid foodId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "dish",
            description = "APIs for dish."
    )
    public static final class UpdateDishController extends AbstractController {

        private final UpdateDishService service;

        @Operation(
                summary = "Update dish API. Returns the new information of dish. API for Restaurant",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food.",
                        content = @Content(schema = @Schema(implementation = UpdateDishRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful update. Returns new information of the dish.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = DishResponse.class))
        )
        @ApiResponse(
                description = "Failed updating of the dish.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @PatchMapping("/dishes/{id}")
        public ResponseEntity<Object> updateDish(@PathVariable Tsid id, @RequestBody UpdateDishRequest request) {
            final DishResponse response = service.update(id, request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class UpdateDishService {

        private final FoodRepository foodRepository;
        private final DishRepository dishRepository;
        private final FirebaseImageService firebaseImageService;
        private final DishMapper dishMapper;

        public DishResponse update(Tsid id, UpdateDishRequest request) {
            var dishId = WhatEatId.builder().id(id).build();
            var foodId = WhatEatId.builder().id(request.getFoodId()).build();
            var dish = dishRepository.findById(dishId);
            if (!dish.isPresent()) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0015)
                        .reason("dish", "Món ăn không tồn tại")
                        .build();
            }
            if (StringUtils.isNotBlank(request.getName())) {
                verifyDishAndSetName(request, dish.get());
            }
            if (StringUtils.isNotBlank(request.getDescription())) {
                dish.get().setDescription(request.getDescription());
            }
            if (request.price != null) {
                dish.get().setPrice(request.getPrice());
            }
            if(request.foodId != null) {
                dish.get().setFood(foodRepository.getReferenceById(foodId));
            }
            FirebaseImageResponse firebaseImageResponse = null;
            try {
                if (StringUtils.isNotBlank(request.getImage())) {
                    firebaseImageResponse = firebaseImageService.uploadBase64Image(request.getImage());
                    dish.get().setImage(firebaseImageResponse.url());
                }
                return dishMapper.convertToDto(dishRepository.saveAndFlush(dish.get()));
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

        private void verifyDishAndSetName(UpdateDishRequest request, Dish dish) {
            if (foodRepository.existsByNameIgnoreCase(request.getName())) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0004)
                        .reason("name", "Tên của món ăn đã tồn tại.")
                        .build();
            }
            dish.setName(request.getName());
        }
    }
}
