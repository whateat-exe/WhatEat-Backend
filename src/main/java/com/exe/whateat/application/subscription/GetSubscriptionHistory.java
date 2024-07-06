package com.exe.whateat.application.subscription;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackerResponse;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackersResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackerResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackersResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetSubscriptionHistory {

    @Getter
    @Setter
    @NoArgsConstructor
    public static final class GetSubscriptionHistoryRequest extends PaginationRequest {

    }

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "subscription",
            description = "APIs for subscription."
    )
    public static final class GetSubscriptionHistoryController extends AbstractController {

        private final GetSubscriptionHistoryService service;

        @GetMapping("/subscriptions/current")
        @Operation(
                summary = "Get current account's subscriptions history API. Returns list of subscriptions."
        )
        @ApiResponse(
                description = "Successful. Returns list of subscriptions.",
                responseCode = "200",
                content = {
                        @Content(schema = @Schema(implementation = RestaurantSubscriptionTrackersResponse.class)),
                        @Content(schema = @Schema(implementation = UserSubscriptionTrackersResponse.class))
                }
        )
        @ApiResponse(
                description = "Failed.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getSubscriptionHistory(@Valid @ParameterObject GetSubscriptionHistoryRequest request) {
            final Object response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @RequiredArgsConstructor
    public static class GetSubscriptionHistoryService {

        private final WhatEatSecurityHelper securityHelper;
        private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;
        private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
        private final WhatEatMapper<UserSubscriptionTracker, UserSubscriptionTrackerResponse> userMapper;
        private final WhatEatMapper<RestaurantSubscriptionTracker, RestaurantSubscriptionTrackerResponse> restaurantMapper;

        public Object get(GetSubscriptionHistoryRequest request) {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0002)
                            .reason("account", "Không xác định được tài khoản.")
                            .build());
            return (account.getRole() == AccountRole.USER)
                    ? getUserSubscriptionHistory(account, request)
                    : getRestaurantSubscriptionHistory(account.getRestaurant(), request);
        }

        private Object getUserSubscriptionHistory(Account account, GetSubscriptionHistoryRequest request) {
            final PageRequest pageable = PageRequest.of(request.getPage(), request.getLimit());
            final List<UserSubscriptionTracker> trackers = userSubscriptionTrackerRepository
                    .findAllByUserId(account.getId(), pageable);
            final long count = userSubscriptionTrackerRepository.countByUserId(account.getId());
            final UserSubscriptionTrackersResponse response = new UserSubscriptionTrackersResponse(trackers.stream()
                    .map(userMapper::convertToDto)
                    .toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }

        private Object getRestaurantSubscriptionHistory(Restaurant restaurant, GetSubscriptionHistoryRequest request) {
            final PageRequest pageable = PageRequest.of(request.getPage(), request.getLimit());
            final List<RestaurantSubscriptionTracker> trackers = restaurantSubscriptionTrackerRepository
                    .findAllByRestaurantId(restaurant.getId(), pageable);
            final long count = restaurantSubscriptionTrackerRepository.countByRestaurantId(restaurant.getId());
            final RestaurantSubscriptionTrackersResponse response = new RestaurantSubscriptionTrackersResponse(trackers.stream()
                    .map(restaurantMapper::convertToDto)
                    .toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }
    }
}
