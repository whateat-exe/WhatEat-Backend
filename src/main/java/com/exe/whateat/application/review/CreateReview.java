package com.exe.whateat.application.review;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.review.mapper.ReviewMapper;
import com.exe.whateat.application.review.response.ReviewResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.random.Rating;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.repository.DishRepository;
import com.exe.whateat.infrastructure.repository.ReviewRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateReview {

    @Data
    @Builder
    public static final class CreateReviewRequest {

        @NotBlank(message = "Nội dung đánh giá bắt buộc phải có.")
        private String feedback;

        @NotNull(message = "Số lượng sao bắt buộc phải có.")
        @Range(min = 1, max = 5, message = "Số lượng sao từ 1 tới 5")
        private Integer stars;

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "review",
            description = "API for reviews"
    )
    public static final class CreateReviewController extends AbstractController {

        private final CreateReviewService createReviewService;

        @PostMapping("/dishes/{id}/reviews")
        @Operation(
                summary = "Create review API",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the review.",
                        content = @Content(schema = @Schema(implementation = CreateReviewRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = DishResponse.class))
        )
        @ApiResponse(
                description = "Failed creation.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createFood(@RequestBody @Valid CreateReviewRequest request, @PathVariable Tsid id) {
            final ReviewResponse response = createReviewService.createReview(request, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreateReviewService {

        private final DishRepository dishRepository;
        private final ReviewRepository reviewRepository;
        private final AccountRepository userRepository;
        private final ReviewMapper reviewMapper;
        private final WhatEatSecurityHelper securityHelper;

        public ReviewResponse createReview(CreateReviewRequest request, Tsid id) {
            final Account acc = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("account", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());

            var userId = acc.getId();
            var dishId = WhatEatId.builder().id(id).build();
            if (reviewRepository.existsByDishIdAndAccountId(dishId, userId)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0017)
                        .reason("name", "Bạn đã tạo đánh giá trước đó cho món này")
                        .build();
            }
            Rating review = Rating.builder()
                    .id(WhatEatId.generate())
                    .stars(request.stars)
                    .feedback(request.feedback)
                    .account(userRepository.getReferenceById(userId))
                    .dish(dishRepository.getReferenceById(dishId))
                    .createdAt(Instant.now())
                    .lastModified(Instant.now())
                    .build();
            return reviewMapper.convertToDto(reviewRepository.saveAndFlush(review));
        }

    }
}
