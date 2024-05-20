package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.food.mapper.FoodMapper;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodTagRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetFoodTag {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtag",
            description = "get a foodtag by id"
    )
    public static final class GetFoodTagController {

        private GetFoodTagService getFoodTagService;

        @GetMapping("/foodtags/{id}")
        @Operation(
                summary = "Get food tag API. Returns the food tag from the ID."
        )
        @ApiResponse(
                description = "Successful. Returns the food tag. ADMIN & MANAGER will return any status",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodTagResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the food tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getFoodTag(@PathVariable Tsid id) {
            var response = getFoodTagService.getFoodTag(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetFoodTagService {

        private FoodTagRepository foodTagRepository;
        private FoodMapper foodMapper;
        private TagMapper tagMapper;

        public FoodTagResponse getFoodTag(Tsid tsid) {
            var foodtag = foodTagRepository.FindByFoodTag_Id(WhatEatId.builder().id(tsid).build());
            return FoodTagResponse
                    .builder()
                    .tsid(foodtag.getId().asTsid())
                    .tagResponse(tagMapper.convertToDto(foodtag.getTag()))
                    .foodResponse(foodMapper.convertToDto(foodtag.getFood()))
                    .build();
        }
    }
}
