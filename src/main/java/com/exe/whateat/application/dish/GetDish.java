package com.exe.whateat.application.dish;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.dish.mapper.DishMapper;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.dish.response.DishesResponse;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QDish;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetDish {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "dish",
            description = "get a dish"
    )
    public static class GetDishController extends AbstractController {

        private GetDishService getDishService;

        @Operation(
                summary = "Get dish API. Returns a dish "
        )
        @ApiResponse(
                description = "Successful. Returns a dish.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = DishesResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the foods.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/dishes/{id}")
        public ResponseEntity<Object> getDishes(@PathVariable Tsid id) {
            var response = getDishService.getDish(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetDishService {

        private DishMapper dishMapper;
        private DishRepository dishRepository;

        public DishResponse getDish(Tsid tsid) {
            var whatEatDishId = WhatEatId.builder().id(tsid).build();
            var dish = dishRepository.findByIdOfDish(whatEatDishId);
            if (dish == null)
                throw  WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0014)
                        .reason("món ăn", "Món ăn không tồn tại")
                        .build();
            return dishMapper.convertToDto(dish);
        }
    }
}
