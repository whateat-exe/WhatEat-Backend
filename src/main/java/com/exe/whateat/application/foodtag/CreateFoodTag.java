package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.mapper.FoodMapper;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.exe.whateat.infrastructure.repository.FoodTagRepository;
import com.exe.whateat.infrastructure.repository.TagRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateFoodTag {

    @Getter
    public static class CreateFoodTagRequest {

        @NotNull(message = "Food Id can not be null")
        private Tsid foodId;

        @NotNull(message = "Tag Id can not be null")
        private Tsid tagId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtag",
            description = "Create food tag"
    )
    public static final class CreateFoodTagController extends AbstractController {

        private CreateFoodTagService createFoodTagService;

        @PostMapping("/foodtags")
        @Operation(
                summary = "Create food tag API. Returns the new information of food and tag. ADMIN/MANAGER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food and tag.",
                        content = @Content(schema = @Schema(implementation = CreateFoodTag.CreateFoodTagRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns new information of the food and tag.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodTagResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the food tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createTag(@RequestBody @Valid CreateFoodTagRequest createFoodTagRequest) {
            var response = createFoodTagService.createFoodTag(createFoodTagRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class CreateFoodTagService {

        private FoodTagRepository foodTagRepository;
        private FoodRepository foodRepository;
        private TagRepository tagRepository;
        private TagMapper tagMapper;
        private FoodMapper foodMapper;

        @SuppressWarnings("java:S3457")
        public FoodTagResponse createFoodTag(CreateFoodTagRequest createFoodTagRequest) {
            //Check food có tồn tại
            var food = foodRepository.findById(WhatEatId.builder().id(createFoodTagRequest.foodId).build());
            if (!food.isPresent())
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0005)
                        .reason("food", String.format("Món ăn không tồn tại."))
                        .build();
            // Check tag co ton tai
            var tag = tagRepository.findById(WhatEatId.builder().id(createFoodTagRequest.tagId).build());
            if (!tag.isPresent())
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("tag", "Tag này không tồn tại")
                        .build();
            //Check duplicate
            var foodTags = foodTagRepository.findByFood(food.get());
            for (var foodTagCheckDuplicated : foodTags) {
                if (createFoodTagRequest.tagId.equals(foodTagCheckDuplicated.getTag().getId().asTsid()))
                    throw WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("food tag duplicated", "Food tag was created before")
                            .build();
            }
            // Create food tag
            FoodTag foodTag = FoodTag
                    .builder()
                    .food(food.get())
                    .tag(tag.get())
                    .id(WhatEatId.generate())
                    .status(ActiveStatus.ACTIVE)
                    .build();
            var foodTagCreated = foodTagRepository.saveAndFlush(foodTag);
            return FoodTagResponse
                    .builder()
                    .tsid(foodTagCreated.getId().asTsid())
                    .tagResponse(tagMapper.convertToDto(foodTag.getTag()))
                    .foodResponse(foodMapper.convertToDto(foodTag.getFood()))
                    .build();
        }
    }
}
