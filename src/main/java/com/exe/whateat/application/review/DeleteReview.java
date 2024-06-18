package com.exe.whateat.application.review;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
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
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeleteReview {

    @RestController
    @AllArgsConstructor
    @Tag(name = "review", description = "API for reviews")
    public static final class DeleteReviewController extends AbstractController {

        private final DeleteReviewService service;

        @DeleteMapping("/reviews/{id}")
        @Operation(
                summary = "Delete review API."
        )
        @ApiResponse(description = "Successful deleted.", responseCode = "200", content = @Content(schema = @Schema(implementation = FoodResponse.class)))
        @ApiResponse(description = "Failed deleting.", responseCode = "400s/500s", content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class)))
        public ResponseEntity<Object> deleteReview(@PathVariable Tsid id) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class DeleteReviewService {

        private ReviewRepository reviewRepository;
        private final WhatEatSecurityHelper securityHelper;

        public void delete(Tsid id) {
            final Account acc = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());

            var review = reviewRepository.findById(new WhatEatId(id))
                    .orElseThrow(() -> WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0016)
                            .reason("id", "Đánh giá không tồn tại.")
                            .build());

            if (!review.getAccount().getEmail().equals(acc.getEmail())) {
                throw WhatEatException.builder().code(WhatEatErrorCode.WEA_0002).reason("name", "Bạn không có quyền thực hiện").build();
            }

            reviewRepository.delete(review);
        }
    }

}
