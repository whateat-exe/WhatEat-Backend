package com.exe.whateat.application.review;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.review.mapper.ReviewMapper;
import com.exe.whateat.application.review.response.ReviewResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.ReviewRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateReview {

    @Data
    @Builder
    public static final class UpdateReviewRequest {

        private String feedback;
        private Integer stars;

    }

    @RestController
    @AllArgsConstructor
    @Tag(name = "review", description = "API for reviews")
    public static final class UpdateReviewController extends AbstractController {

        private final UpdateReviewService service;

        @PatchMapping("/reviews/{id}")
        @Operation(summary = "Update review API.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Information of the review.", content = @Content(schema = @Schema(implementation = UpdateReviewRequest.class))))
        @ApiResponse(description = "Successful update.", responseCode = "200", content = @Content(schema = @Schema(implementation = FoodResponse.class)))
        @ApiResponse(description = "Failed update.", responseCode = "400s/500s", content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class)))
        public ResponseEntity<Object> update(@PathVariable Tsid id, @RequestBody UpdateReviewRequest request) {
            final ReviewResponse response = service.update(id, request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class UpdateReviewService {

        private final ReviewRepository reviewRepository;
        private final ReviewMapper reviewMapper;
        private final WhatEatSecurityHelper securityHelper;

        public ReviewResponse update(Tsid id, UpdateReviewRequest request) {
            final Account acc = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());

            var reviewId = WhatEatId.builder().id(id).build();
            var review = reviewRepository.findById(reviewId);

            if (request.stars < 0 || request.stars > 5) {
                throw WhatEatException.builder().code(WhatEatErrorCode.WEV_0000).reason("stars", "Số lượng sao từ 1 tới 5").build();
            }

            if (review.isEmpty()) {
                throw WhatEatException.builder().code(WhatEatErrorCode.WEB_0016).reason("name", "Đánh giá không tồn tại.").build();
            }

            if (!review.get().getAccount().getEmail().equals(acc.getEmail())) {
                throw WhatEatException.builder().code(WhatEatErrorCode.WEA_0002).reason("name", "Bạn không có quyền thực hiện").build();
            }

            if (StringUtils.isNotBlank(request.getFeedback())) {
                review.get().setFeedback(request.feedback);
            }

            if (request.stars != null) {
                review.get().setStars(request.stars);
            }

            review.get().setLastModified(Instant.now());

            return reviewMapper.convertToDto(reviewRepository.saveAndFlush(review.get()));
        }
    }
}
