package com.exe.whateat.application.dish;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.dish.mapper.DishMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.Money;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public final class CreateDish {

    @Data
    @Builder
    public static final class CreateDishRequest {

        @NotBlank(message = "Tên của món ăn bắt buộc phải có.")
        private String name;

        @NotBlank(message = "Ảnh của món ăn bắt buộc phải có.")
        private String image;

        @NotBlank(message = "Mô tả của món ăn bắt buộc phải có.")
        private String description;

        @NotNull(message = "Tiền của món ăn bắt buộc phải có.")
        private Money price;

        @NotNull
        private Tsid foodId;

        @NotNull(message = "nhà hàng của món ăn bắt buộc phải có.")
        private Tsid restaurantId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "dish",
            description = "API for create a dish"
    )
    public static final class CreateDishController extends AbstractController {

        private final CreateDishService createDishService;

        @PostMapping("/dishes")
        @Operation(
                summary = "Create dish API. Returns the new information of dish.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food.",
                        content = @Content(schema = @Schema(implementation = CreateDishRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns new information of the food.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = DishResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the food.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createFood(@RequestBody @Valid CreateDishRequest createDishRequest) {
            final DishResponse response = createDishService.createDish(createDishRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreateDishService {

        private final DishRepository dishRepository;
        private final FoodRepository foodRepository;
        private final RestaurantRepository restaurantRepository;
        private final FirebaseImageService firebaseImageService;
        private final DishMapper dishMapper;

        public DishResponse createDish(CreateDishRequest request) {
            var foodId = WhatEatId.builder().id(request.getFoodId()).build();
            var restaurantId = WhatEatId.builder().id(request.getRestaurantId()).build();
            if (dishRepository.existsByNameIgnoreCase(request.getName())) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0014)
                        .reason("name", "Tên của món ăn đã tồn tại.")
                        .build();
            }
            Dish dish = Dish.builder()
                    .id(WhatEatId.generate())
                    .name(request.getName())
                    .status(ActiveStatus.ACTIVE)
                    .description(request.description)
                    .price(request.getPrice())
                    .food(foodRepository.getReferenceById(foodId))
                    .restaurant(restaurantRepository.getReferenceById(restaurantId))
                    .build();
            FirebaseImageResponse firebaseImageResponse = null;
            try {
                firebaseImageResponse = firebaseImageService.uploadBase64Image(request.getImage());
                dish.setImage(firebaseImageResponse.url());
                return dishMapper.convertToDto(dishRepository.saveAndFlush(dish));
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
                        .reason("dish", "Lỗi trong việc tạo món ăn")
                        .build();
            }
        }
    }
}
