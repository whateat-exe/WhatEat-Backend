package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.FoodTag;
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
            name = "foodtags",
            description = "APIs for food tags."
    )
    public static final class GetFoodTagController extends AbstractController {

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
        private WhatEatMapper<FoodTag, FoodTagResponse> mapper;

        public FoodTagResponse getFoodTag(Tsid id) {
            var foodtag = foodTagRepository.findByIdPopulated(new WhatEatId(id))
                    .orElseThrow(() -> WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0009)
                            .reason("foodTag", "Món ăn với nhãn trên không tồn tại.")
                            .build());
            return mapper.convertToDto(foodtag);
        }
    }
}
