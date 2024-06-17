package com.exe.whateat.application.review;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.review.mapper.ReviewMapper;
import com.exe.whateat.application.review.response.ReviewsResponse;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.random.QRating;
import com.exe.whateat.entity.random.Rating;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetReviews {

    @NoArgsConstructor
    public static final class GetReviewsRequest extends PaginationRequest {

    }

    @RestController
    @AllArgsConstructor
    @Tag(name = "review", description = "API for reviews")
    public static class GetReviewsController extends AbstractController {

        private GetReviewsService getReviewsService;

        @Operation(
                summary = "Get reviews API. Returns the list of reviews paginated"
        )
        @ApiResponse(
                description = "Successful. Returns list of the reviews.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = ReviewsResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the reviews.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/reviews")
        public ResponseEntity<Object> getDishes(@Valid GetReviewsRequest request) {
            var response = getReviewsService.get(request);
            return ResponseEntity.ok(response);
        }
    }


    @Service
    @AllArgsConstructor
    public static class GetReviewsService {

        private ReviewMapper reviewMapper;
        private EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        public ReviewsResponse get(GetReviewsRequest request) {
            final QRating qReview = QRating.rating;
            BlazeJPAQuery<Rating> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory);
            final List<Rating> reviews = query
                    .select(qReview)
                    .from(qReview)
                    .leftJoin(qReview.dish).fetchJoin()
                    .leftJoin(qReview.account).fetchJoin()
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .fetch();
            final long count = new BlazeJPAQuery<FoodTag>(entityManager, criteriaBuilderFactory)
                    .select(qReview)
                    .from(qReview)
                    .fetchCount();
            final ReviewsResponse response = new ReviewsResponse(reviews.stream().map(reviewMapper::convertToDto).toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }

    }
}
