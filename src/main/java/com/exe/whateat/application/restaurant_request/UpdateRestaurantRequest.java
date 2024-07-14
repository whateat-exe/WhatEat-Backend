package com.exe.whateat.application.restaurant_request;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.restaurant_request.mapper.RequestMapper;
import com.exe.whateat.application.restaurant_request.response.RequestResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.request.RestaurantRequest;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantRequestRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateRestaurantRequest {

    @Getter
    @Setter
    public static class UpdateRestaurantRequestDishRequest {

        private String title;
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

        @PatchMapping("/restaurant-requests/{id}")
        @Operation(
                summary = "Create restaurant request API",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the restaurant request.",
                        content = @Content(schema = @Schema(implementation = UpdateRestaurantRequestDishRequest.class))
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
        public ResponseEntity<Object> update(@RequestBody @Valid UpdateRestaurantRequestDishRequest request, @PathVariable Tsid id) {
            var response = service.update(request, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class CreateRestaurantRequestDishService {

        private final RestaurantRequestRepository restaurantRequestRepository;
        private WhatEatSecurityHelper securityHelper;
        private final RequestMapper requestMapper;

        public RequestResponse update(UpdateRestaurantRequestDishRequest request, Tsid id) {
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

            // update request
            WhatEatId whatEatId = new WhatEatId(id);
            RestaurantRequest restaurantRequest = restaurantRequestRepository.findById(whatEatId)
                    .orElseThrow(() -> {
                        throw WhatEatException.builder()
                                .code(WhatEatErrorCode.WES_0001)
                                .reason("restaurant_request", "Lỗi gửi id.")
                                .build();
                    });
            if (request.getTitle() != null) {
                restaurantRequest.setTitle(request.getTitle());
            }
            if (request.getContent() != null) {
                restaurantRequest.setContent(request.getContent());
            }
            return requestMapper.convertToDto(restaurantRequestRepository.save(restaurantRequest));
        }
    }
}
