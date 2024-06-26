package com.exe.whateat.application.review;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.review.mapper.ReviewMapper;
import com.exe.whateat.application.review.request.ReviewFilter;
import com.exe.whateat.application.review.response.ReviewsResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.random.QRating;
import com.exe.whateat.entity.random.Rating;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.DishRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetReviewsByDish {

    @Data
    public static final class GetReviewsByDishRequest extends PaginationRequest {

        private ReviewFilter asc;
        private ReviewFilter desc;

    }

    @RestController
    @AllArgsConstructor
    @Tag(name = "review", description = "API for reviews")
    public static class GetReviewsController extends AbstractController {

        private GetReviewsByDishService getReviewsByDishService;

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
        @GetMapping("/dishes/{id}/reviews")
        public ResponseEntity<Object> getDishes(@ParameterObject GetReviewsByDishRequest request, @PathVariable Tsid id) {
            var response = getReviewsByDishService.get(request, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetReviewsByDishService {
        private ReviewMapper reviewMapper;
        private EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final DishRepository dishRepository;

        public ReviewsResponse get(GetReviewsByDishRequest request, Tsid tsid) {
            if (request.getAsc() != null && request.getDesc() != null) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEV_0000)
                        .reason("filter", "Vui lòng chỉ sử dụng 1 filter: asc hoặc desc.")
                        .build();
            }

            final WhatEatId whatEatDishId = new WhatEatId(tsid);

            if (!dishRepository.existsById(whatEatDishId)) {
                throw WhatEatException.builder().code(WhatEatErrorCode.WEB_0015).reason("name", "Món ăn không tồn tại.").build();
            }

            final QRating qReview = QRating.rating;
            BooleanExpression predicates = qReview.isNotNull();
            predicates = predicates.and(qReview.dish.id.eq(whatEatDishId));

            JPAQuery<Rating> query = new JPAQuery<>(entityManager);
            List<Rating> reviews;

             if (ReviewFilter.STAR.equals(request.getAsc()) || ReviewFilter.STAR.equals(request.getDesc())) {
                query.select(qReview)
                        .from(qReview)
                        .where(predicates);

                if (ReviewFilter.STAR.equals(request.getAsc())) {
                    query.orderBy(qReview.stars.asc());
                } else {
                    query.orderBy(qReview.stars.desc());
                }
            } else if (ReviewFilter.TIME.equals(request.getAsc()) || ReviewFilter.TIME.equals(request.getDesc())) {
                 query.select(qReview)
                         .from(qReview)
                         .where(predicates);

                if (ReviewFilter.TIME.equals(request.getAsc())) {
                    query.orderBy(qReview.lastModified.asc());
                } else {
                    query.orderBy(qReview.lastModified.desc());
                }
            } else {
                query.select(qReview)
                        .from(qReview)
                        .where(predicates);
            }

            reviews = query
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .fetch();

             final long count = new BlazeJPAQuery<Rating>(entityManager, criteriaBuilderFactory)
                    .select(qReview)
                    .from(qReview)
                    .where(predicates)
                    .fetchCount();

            final ReviewsResponse response = new ReviewsResponse(reviews.stream().map(reviewMapper::convertToDto).toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }
    }

}
