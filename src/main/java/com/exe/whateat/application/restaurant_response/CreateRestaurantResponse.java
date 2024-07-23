package com.exe.whateat.application.restaurant_response;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.restaurant_response.mapper.RestaurantResponseMapper;
import com.exe.whateat.application.restaurant_response.response.RestaurantResponseResponse;
import com.exe.whateat.application.restaurant_response.response.RestaurantResponsesResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RequestStatus;
import com.exe.whateat.entity.request.RestaurantRequestResponse;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRequestRepository;
import com.exe.whateat.infrastructure.repository.RestaurantRequestResponseRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
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
public class CreateRestaurantResponse {

    @Getter
    @Setter
    public static class CreateRestaurantResponseDishRequest {

        @NotNull(message = "Title is required ")
        private String title;

        @NotNull(message = "content is required ")
        private String content;

        @NotNull(message = "status is required ")
        private RequestStatus status;

        @NotNull(message = "status is required ")
        private Tsid restaurantRequestId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "restaurant_response",
            description = "APIs for restaurant response."
    )
    public static final class CreateRestaurantResponseDishController extends AbstractController {

        private CreateRestaurantResponseDishService service;

        @PostMapping("/restaurant-responses")
        @Operation(
                summary = "Create restaurant response API",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the restaurant response.",
                        content = @Content(schema = @Schema(implementation = CreateRestaurantResponseDishRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = RestaurantResponsesResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the restaurant request.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> create(@RequestBody @Valid CreateRestaurantResponseDishRequest request) {
            var response = service.create(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class CreateRestaurantResponseDishService {

        private final RestaurantRequestResponseRepository restaurantRequestResponseRepository;
        private final RestaurantRequestRepository restaurantRequest;
        private WhatEatSecurityHelper securityHelper;
        private final RestaurantResponseMapper restaurantResponseMapper;

        public RestaurantResponseResponse create(CreateRestaurantResponseDishRequest request) {
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

            // create request
            WhatEatId whatEatRestaurantReqeustId = new WhatEatId(request.restaurantRequestId);
            RestaurantRequestResponse restaurantRequestResponse = RestaurantRequestResponse.builder()
                    .id(WhatEatId.generate())
                    .createdAt(Instant.now())
                    .content(request.content)
                    .status(request.status)
                    .title(request.title)
                    .restaurantRequest(restaurantRequest.getReferenceById(whatEatRestaurantReqeustId))
                    .build();
            var response = restaurantResponseMapper.convertToDto(restaurantRequestResponseRepository.save(restaurantRequestResponse));
            return response;
        }
    }
}
