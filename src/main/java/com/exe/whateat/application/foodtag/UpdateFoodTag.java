package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.mapper.FoodMapper;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateFoodTag {

    @Data
    public static class UpdateFoodTagRequest {

        private Tsid foodId;
        private Tsid tagId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtag",
            description = "update food tag"
    )
    public static final class UpdateFoodTagController extends AbstractController {

        private UpdateFoodTagService updateFoodTagService;

        @PatchMapping("/foodtags/{id}")
        @Operation(
                summary = "Create food tag API. Returns the new information of food and tag. ADMIN/MANAGER only.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the food and tag.",
                        content = @Content(schema = @Schema(implementation = UpdateFoodTag.UpdateFoodTagRequest.class))
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
        public ResponseEntity<Object> updateFoodTag(@PathVariable Tsid id, @RequestBody UpdateFoodTagRequest updateFoodTagRequest) {
            var response = updateFoodTagService.updateFoodTag(id, updateFoodTagRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class UpdateFoodTagService {

        private FoodTagRepository foodTagRepository;
        private FoodRepository foodRepository;
        private TagRepository tagRepository;
        private FoodMapper foodMapper;
        private TagMapper tagMapper;

        public FoodTagResponse updateFoodTag(Tsid tsid, UpdateFoodTagRequest updateFoodTagRequest) {
            var foodTag = foodTagRepository.findById(WhatEatId.builder().id(tsid).build());
            if (!foodTag.isPresent())
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("lỗi gửi id food tag", "gửi id sai hoặc không đúng định dạng")
                        .build();
            // check food is valid
            Optional<Food> food = Optional.empty();
            if (updateFoodTagRequest.foodId != null) {
                food = foodRepository.findById(WhatEatId.builder().id(updateFoodTagRequest.foodId).build());
                if (food.isEmpty())
                    throw WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("lỗi gửi id food", "gửi id sai hoặc không đúng định dạng của food")
                            .build();
                foodTag.get().setFood(food.get());
            }
            // Check tag is valid
            Optional<com.exe.whateat.entity.food.Tag> tag = null;
            if (updateFoodTagRequest.tagId != null) {
                tag = tagRepository.findById(WhatEatId.builder().id(updateFoodTagRequest.foodId).build());
                if (tag.isEmpty())
                    throw WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("lỗi gửi id tag", "gửi id sai hoặc không đúng định dạng của tag")
                            .build();
                foodTag.get().setTag(tag.get());
            }
            FoodTag foodTagUpdated = null;
            if (updateFoodTagRequest.tagId != null || updateFoodTagRequest.foodId != null)
                foodTagUpdated = foodTagRepository.saveAndFlush(foodTag.get());
            // ??? Why not checking null
            return FoodTagResponse
                    .builder()
                    .tsid(foodTagUpdated.getId().asTsid())
                    .foodResponse(foodMapper.convertToDto(food.get()))
                    .tagResponse(tagMapper.convertToDto(tag.get()))
                    .build();
        }
    }
}
