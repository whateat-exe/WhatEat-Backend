package com.exe.whateat.application.review;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.review.response.SummaryResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
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
public final class GetSummaryOfReviewsByDish {

    @RestController
    @AllArgsConstructor
    @Tag(name = "review", description = "API for reviews")
    public static class GetSummaryOfReviewsByDishController extends AbstractController {

        private GetSummaryOfReviewsByDishService service;

        @Operation(
                summary = "Get summary of reviews API."
        )
        @ApiResponse(
                description = "Successful.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = SummaryResponse.class))
        )
        @ApiResponse(
                description = "Failed.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/dishes/{id}/reviews/summary")
        public ResponseEntity<Object> getSummary(@PathVariable Tsid id) {
            var response = service.get(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetSummaryOfReviewsByDishService {
        private final DishRepository dishRepository;

        public SummaryResponse get(Tsid tsid) {
            final WhatEatId whatEatDishId = new WhatEatId(tsid);

            if (!dishRepository.existsById(whatEatDishId)) {
                throw WhatEatException.builder().code(WhatEatErrorCode.WEB_0015).reason("name", "Món ăn không tồn tại.").build();
            }

            return SummaryResponse.builder()
                    .avgReview(formatAvg(dishRepository.findAverageRatingByDishId(whatEatDishId)))
                    .numOfReview(dishRepository.countRatingsByDishId(whatEatDishId))
                    .numOfFiveStar(dishRepository.countRatingsByDishIdAndStars(whatEatDishId,5))
                    .numOfFourStar(dishRepository.countRatingsByDishIdAndStars(whatEatDishId,4))
                    .numOfThreeStar(dishRepository.countRatingsByDishIdAndStars(whatEatDishId,3))
                    .numOfTwoStar(dishRepository.countRatingsByDishIdAndStars(whatEatDishId,2))
                    .numOfOneStar(dishRepository.countRatingsByDishIdAndStars(whatEatDishId,1))
                    .build();
        }

        private Double formatAvg(Double avg) {
            return avg != null ? Math.round(avg * 10) / 10.0 : null;
        }
    }

}
