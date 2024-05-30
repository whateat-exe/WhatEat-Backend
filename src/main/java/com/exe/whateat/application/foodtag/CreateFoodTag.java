package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
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
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateFoodTag {

    @Getter
    @Setter
    public static class CreateFoodTagRequest {

        @NotNull(message = "Food Id can not be null")
        private Tsid foodId;

        @NotNull(message = "Tag Id can not be null")
        private Tsid tagId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtags",
            description = "APIs for food tags."
    )
    public static final class CreateFoodTagController extends AbstractController {

        private CreateFoodTagService service;

        @PostMapping("/foodtags")
        @Operation(
                summary = "Create food tag API. Returns the new information of food and tag. ADMIN/MANAGER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food and tag.",
                        content = @Content(schema = @Schema(implementation = CreateFoodTagRequest.class))
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
            var response = service.createFoodTag(createFoodTagRequest);
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
        private WhatEatMapper<FoodTag, FoodTagResponse> mapper;

        public FoodTagResponse createFoodTag(CreateFoodTagRequest request) {
            final WhatEatId foodId = new WhatEatId(request.getFoodId());
            final WhatEatId tagId = new WhatEatId(request.getTagId());
            if (foodTagRepository.foodTagAlreadyExists(foodId, tagId)) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0009)
                        .reason("foodTag", "Món ăn với nhãn trên đã tồn tại.")
                        .build();
            }
            FoodTag foodTag = FoodTag.builder()
                    .id(WhatEatId.generate())
                    .food(foodRepository.getReferenceById(foodId))
                    .tag(tagRepository.getReferenceById(tagId))
                    .status(ActiveStatus.ACTIVE)
                    .build();
            foodTag = foodTagRepository.save(foodTag);
            return mapper.convertToDto(foodTag);
        }
    }
}
