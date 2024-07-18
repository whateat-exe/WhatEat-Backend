package com.exe.whateat.application.subscription;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.subscription.request.SubscriptionFilter;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackerResponse;
import com.exe.whateat.application.subscription.response.RestaurantSubscriptionTrackersResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackerResponse;
import com.exe.whateat.application.subscription.response.UserSubscriptionTrackersResponse;
import com.exe.whateat.entity.account.Account;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.entity.subscription.QRestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.QUserSubscriptionTracker;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.UserSubscriptionTracker;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionTrackerRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public final class GetPaidSubscriptionHistory {

    @Getter
    @Setter
    @NoArgsConstructor
    public static final class GetPaidSubscriptionHistoryRequest extends PaginationRequest {

        private SubscriptionFilter paidDate;

    }

    @RestController
    @RequiredArgsConstructor
    @Tag(
            name = "subscription",
            description = "APIs for subscription."
    )
    public static final class GetPaidSubscriptionHistoryController extends AbstractController {

        private final GetPaidSubscriptionHistoryService service;

        @GetMapping("/subscriptions/paid/current")
        @Operation(
                summary = "Get current account's paid subscriptions history API. Returns list of subscriptions."
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
        public ResponseEntity<Object> getSubscriptionHistory(@Valid @ParameterObject GetPaidSubscriptionHistoryRequest request) {
            final Object response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }


    @Service
    @Transactional(rollbackOn = Exception.class)
    @RequiredArgsConstructor
    public static class GetPaidSubscriptionHistoryService {

        private final WhatEatSecurityHelper securityHelper;
        private final UserSubscriptionTrackerRepository userSubscriptionTrackerRepository;
        private final RestaurantSubscriptionTrackerRepository restaurantSubscriptionTrackerRepository;
        private final WhatEatMapper<UserSubscriptionTracker, UserSubscriptionTrackerResponse> userMapper;
        private final WhatEatMapper<RestaurantSubscriptionTracker, RestaurantSubscriptionTrackerResponse> restaurantMapper;
        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        public Object get(GetPaidSubscriptionHistoryRequest request) {
            final Account account = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0002)
                            .reason("account", "Không xác định được tài khoản.")
                            .build());
            return (account.getRole() == AccountRole.USER)
                    ? getUserSubscriptionHistory(account, request)
                    : getRestaurantSubscriptionHistory(account.getRestaurant(), request);
        }

        private Object getUserSubscriptionHistory(Account account, GetPaidSubscriptionHistoryRequest request) {
            final QUserSubscriptionTracker qTracker = QUserSubscriptionTracker.userSubscriptionTracker;

            OrderSpecifier<?> orderSpecifier = qTracker.validityStart.desc();
            if (request.paidDate != null && request.paidDate == SubscriptionFilter.ASC) {
                orderSpecifier = qTracker.validityStart.asc();
            }
            BooleanExpression predicates = qTracker.isNotNull();
            predicates = predicates.and(qTracker.user.id.eq(account.getId()));

            predicates = predicates.and(qTracker.validityStart.isNotNull());

            JPAQuery<UserSubscriptionTracker> query = new JPAQuery<>(entityManager);
            List<UserSubscriptionTracker> trackers = query.select(qTracker)
                    .from(qTracker)
                    .where(predicates)
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .orderBy(orderSpecifier)
                    .fetch();

            final long count = new BlazeJPAQuery<UserSubscriptionTracker>(entityManager, criteriaBuilderFactory)
                    .select(qTracker)
                    .from(qTracker)
                    .where(predicates)
                    .fetchCount();

            final UserSubscriptionTrackersResponse response = new UserSubscriptionTrackersResponse(trackers.stream()
                    .map(userMapper::convertToDto)
                    .toList(), count, count);

            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }

        private Object getRestaurantSubscriptionHistory(Restaurant restaurant, GetPaidSubscriptionHistoryRequest request) {
            final QRestaurantSubscriptionTracker qTracker = QRestaurantSubscriptionTracker.restaurantSubscriptionTracker;

            OrderSpecifier<?> orderSpecifier = qTracker.validityStart.desc();
            if (request.paidDate != null && request.paidDate == SubscriptionFilter.ASC) {
                orderSpecifier = qTracker.validityStart.asc();
            }
            BooleanExpression predicates = qTracker.isNotNull();
            predicates = predicates.and(qTracker.restaurant.id.eq(restaurant.getId()));

            predicates = predicates.and(qTracker.validityStart.isNotNull());

            JPAQuery<RestaurantSubscriptionTracker> query = new JPAQuery<>(entityManager);
            List<RestaurantSubscriptionTracker> trackers = query.select(qTracker)
                    .from(qTracker)
                    .where(predicates)
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .orderBy(orderSpecifier)
                    .fetch();

            final long count = new BlazeJPAQuery<RestaurantSubscriptionTracker>(entityManager, criteriaBuilderFactory)
                    .select(qTracker)
                    .from(qTracker)
                    .where(predicates)
                    .fetchCount();

            final RestaurantSubscriptionTrackersResponse response = new RestaurantSubscriptionTrackersResponse(trackers.stream()
                    .map(restaurantMapper::convertToDto)
                    .toList(), count, count);

            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }
    }

}
