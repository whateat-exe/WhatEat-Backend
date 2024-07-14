package com.exe.whateat.application.restaurant_request;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.image.FirebaseImageResponse;
import com.exe.whateat.application.image.FirebaseImageService;
import com.exe.whateat.application.post.CreatePost;
import com.exe.whateat.application.post.mapper.PostMapper;
import com.exe.whateat.application.post.response.PostResponse;
import com.exe.whateat.application.restaurant_request.mapper.RequestMapper;
import com.exe.whateat.application.restaurant_request.response.RequestResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.post.PostImage;
import com.exe.whateat.entity.request.RequestCreateTracker;
import com.exe.whateat.entity.request.RequestType;
import com.exe.whateat.entity.request.RestaurantRequest;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostImageRepository;
import com.exe.whateat.infrastructure.repository.PostRepository;
import com.exe.whateat.infrastructure.repository.RequestCreateTrackerRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRequestRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateRestaurantRequest {

    @Getter
    @Setter
    public static class CreateRestaurantRequestDishRequest {

        @NotNull(message = "Title is required ")
        private String title;

        @NotNull(message = "content is required ")
        private String content;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant_request",
            description = "APIs for restaurant request."
    )
    public static final class CreateRestaurantRequestDishController extends AbstractController {

        private CreateRestaurantRequestDishService service;

        @PostMapping("/restaurant-requests")
        @Operation(
                summary = "Create restaurant request API",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the restaurant request.",
                        content = @Content(schema = @Schema(implementation = CreateRestaurantRequestDishRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RequestResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the restaurant request.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> create(@RequestBody @Valid CreateRestaurantRequestDishRequest request) {
            var response = service.createPost(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class CreateRestaurantRequestDishService {

        private final RequestCreateTrackerRepository requestCreateTrackerRepository;
        private final RestaurantRepository restaurantRepository;
        private final RestaurantRequestRepository restaurantRequestRepository;
        private WhatEatSecurityHelper securityHelper;
        private final RequestMapper requestMapper;

        public RequestResponse createPost(CreateRestaurantRequestDishRequest request) {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WEA_0013)
                            .reason("restaurant", "Không xác định được tài khoản đang thực hiện hành động này.")
                            .build());
            if (!account.getRole().equals(AccountRole.RESTAURANT))
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEA_0013)
                        .reason("account", "Không phải là nhà hàng.")
                        .build();
            final RequestCreateTracker requestCreateTracker = requestCreateTrackerRepository.findByRestaurantIdAndStatus(account.getRestaurant().getId())
                    .orElseThrow(() -> {
                        throw WhatEatException.builder()
                                .code(WhatEatErrorCode.WEB_0021)
                                .reason("restaurant_tracker", "tài khoản chưa đăng kí gói")
                                .build();
                    });
            if (requestCreateTracker.getNumberOfRequestedDish() == requestCreateTracker.getMaxNumberOfCreateDish())
                throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WEB_0025)
                    .reason("restaurant_tracker", "Gửi vướt giới hạn của gói")
                    .build();

            // create request
            RestaurantRequest restaurantRequest = RestaurantRequest.builder()
                    .id(WhatEatId.generate())
                    .createdAt(Instant.now())
                    .content(request.content)
                    .type(RequestType.NEW_FOOD)
                    .title(request.title)
                    .restaurant(restaurantRepository.getReferenceById(account.getRestaurant().getId()))
                    .build();
            var response = requestMapper.convertToDto(restaurantRequestRepository.save(restaurantRequest));
            return response;
        }
    }
}
