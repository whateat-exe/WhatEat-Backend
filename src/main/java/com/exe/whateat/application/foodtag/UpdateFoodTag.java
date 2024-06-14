package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateFoodTag {

    @Getter
    @Setter
    public static class UpdateFoodTagRequest {

        private Tsid foodId;
        private Tsid tagId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtags",
            description = "APIs for food tags."
    )
    public static final class UpdateFoodTagController extends AbstractController {

        private UpdateFoodTagService updateFoodTagService;

        @PatchMapping("/foodtags/{id}")
        @Operation(
                summary = "Create food tag API. Returns the new information of food and tag. ADMIN/MANAGER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food and tag.",
                        content = @Content(schema = @Schema(implementation = UpdateFoodTagRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful update. Returns new information of the food and tag.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodTagResponse.class))
        )
        @ApiResponse(
                description = "Failed updating of the food tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> updateFoodTag(@PathVariable Tsid id, @RequestBody UpdateFoodTagRequest request) {
            var response = updateFoodTagService.updateFoodTag(id, request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class UpdateFoodTagService {

        private FoodTagRepository foodTagRepository;
        private FoodRepository foodRepository;
        private TagRepository tagRepository;
        private WhatEatMapper<FoodTag, FoodTagResponse> mapper;

        public FoodTagResponse updateFoodTag(Tsid id, UpdateFoodTagRequest request) {
            final WhatEatId foodTagId = new WhatEatId(id);
            FoodTag foodTag = foodTagRepository.findByIdPopulated(foodTagId)
                    .orElseThrow(() -> WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0009)
                            .reason("foodTag", "Món ăn với nhãn trên không tồn tại.")
                            .build());
            final WhatEatId foodId = new WhatEatId(request.getFoodId());
            final WhatEatId tagId = new WhatEatId(request.getTagId());
            if (Objects.equals(foodTag.getFood().getId(), foodId) && Objects.equals(foodTag.getTag().getId(), tagId)) {
                return mapper.convertToDto(foodTag);
            }
            if (foodTagRepository.foodTagAlreadyExists(foodId, tagId)) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0009)
                        .reason("foodTag", "Món ăn với nhãn trên đã tồn tại.")
                        .build();
            }
            foodTag.setFood(foodRepository.getReferenceById(foodId));
            foodTag.setTag(tagRepository.getReferenceById(tagId));
            foodTag = foodTagRepository.save(foodTag);
            return mapper.convertToDto(foodTag);
        }
    }
}
