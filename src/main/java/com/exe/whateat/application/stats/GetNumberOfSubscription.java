package com.exe.whateat.application.stats;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.subscription.QRestaurantSubscriptionTracker;
import com.exe.whateat.entity.subscription.QUserSubscriptionTracker;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import com.exe.whateat.infrastructure.repository.RestaurantSubscriptionRepository;
import com.exe.whateat.infrastructure.repository.UserSubscriptionRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetNumberOfSubscription {

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfSubscriptionRequest {

        @Schema(type = "string", example = "01-01-2024")
        private String start;
        @Schema(type = "string", example = "01-01-2024")
        private String end;

    }

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfSubscriptionResponse {
        private long silver;
        private long gold;
        private long diamond;
        private long vip;
    }

    @RestController
    @AllArgsConstructor
    @Tag(name = "stats", description = "API for stats")
    public static class GetNumberOfSubscriptionController extends AbstractController {

        private GetNumberOfSubscriptionService service;

        @Operation(
                summary = "Get number of subscription in a time range."
        )
        @GetMapping("/stats/number-of-subscription")
        public ResponseEntity<Object> get(@ParameterObject GetNumberOfSubscriptionRequest request) {
            GetNumberOfSubscriptionResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    public static class GetNumberOfSubscriptionService {

        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final UserSubscriptionRepository userSubscriptionRepository;
        private final RestaurantSubscriptionRepository restaurantSubscriptionRepository;

        @Value("${whateat.tsid.epoch}")
        private long epoch;

        public GetNumberOfSubscriptionService(EntityManager entityManager, CriteriaBuilderFactory criteriaBuilderFactory, UserSubscriptionRepository userSubscriptionRepository, RestaurantSubscriptionRepository restaurantSubscriptionRepository) {
            this.entityManager = entityManager;
            this.criteriaBuilderFactory = criteriaBuilderFactory;
            this.userSubscriptionRepository = userSubscriptionRepository;
            this.restaurantSubscriptionRepository = restaurantSubscriptionRepository;
        }

        public GetNumberOfSubscriptionResponse get(GetNumberOfSubscriptionRequest request) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            validateDateFormat(request.start);
            validateDateFormat(request.end);

            LocalDate startDate = LocalDate.parse(request.start, formatter);
            LocalDate endDate = LocalDate.parse(request.end, formatter);

            Instant start = startDate.atStartOfDay(ZoneOffset.UTC).minusHours(7).toInstant();
            Instant end = endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).minusHours(7).toInstant();

            if (start.isAfter(end)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0024)
                        .reason("stats", "Ngày bắt đầu lớn hơn ngày kết thúc")
                        .build();
            }

            final QRestaurantSubscriptionTracker qRestaurantSubscriptionTracker = QRestaurantSubscriptionTracker.restaurantSubscriptionTracker;

            BooleanExpression predicatesRestaurant = qRestaurantSubscriptionTracker.validityStart.isNotNull().and(qRestaurantSubscriptionTracker.validityStart.between(start, end));

            BooleanExpression predicatesSilver = predicatesRestaurant.and(qRestaurantSubscriptionTracker.subscription.type.eq(RestaurantSubscriptionType.SILVER));

            final long countSilver = new BlazeJPAQuery<Post>(entityManager, criteriaBuilderFactory)
                    .select(qRestaurantSubscriptionTracker)
                    .from(qRestaurantSubscriptionTracker)
                    .where(predicatesSilver)
                    .fetchCount();

            BooleanExpression predicatesGold = predicatesRestaurant.and(qRestaurantSubscriptionTracker.subscription.type.eq(RestaurantSubscriptionType.GOLD));

            final long countGold = new BlazeJPAQuery<Post>(entityManager, criteriaBuilderFactory)
                    .select(qRestaurantSubscriptionTracker)
                    .from(qRestaurantSubscriptionTracker)
                    .where(predicatesGold)
                    .fetchCount();

            BooleanExpression predicatesDiamond = predicatesRestaurant.and(qRestaurantSubscriptionTracker.subscription.type.eq(RestaurantSubscriptionType.DIAMOND));

            final long countDiamond = new BlazeJPAQuery<Post>(entityManager, criteriaBuilderFactory)
                    .select(qRestaurantSubscriptionTracker)
                    .from(qRestaurantSubscriptionTracker)
                    .where(predicatesDiamond)
                    .fetchCount();

            final QUserSubscriptionTracker qUserSubscriptionTracker = QUserSubscriptionTracker.userSubscriptionTracker;
            BooleanExpression predicatesVip = qUserSubscriptionTracker.validityStart.isNotNull().and(qUserSubscriptionTracker.validityStart.between(start,end));

            final long countVip = new BlazeJPAQuery<Post>(entityManager, criteriaBuilderFactory)
                    .select(qUserSubscriptionTracker)
                    .from(qUserSubscriptionTracker)
                    .where(predicatesVip)
                    .fetchCount();

            GetNumberOfSubscriptionResponse response = GetNumberOfSubscriptionResponse.builder()
                    .silver(countSilver)
                    .gold(countGold)
                    .diamond(countDiamond)
                    .vip(countVip)
                    .build();
            return response;
        }

        private void validateDateFormat(String dateString) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                formatter.parse(dateString);
            } catch (DateTimeParseException e) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0024)
                        .reason("stats", "Ngày tháng không hợp lệ")
                        .build();
            }
        }

    }

}
