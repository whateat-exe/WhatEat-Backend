package com.exe.whateat.application.stats;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.post.QPost;
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
public final class GetNumberOfPostCreated {

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfPostCreatedRequest {

        @Schema(type = "string", example = "01-01-2024")
        private String start;
        @Schema(type = "string", example = "01-01-2024")
        private String end;

    }

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfPostCreatedResponse {
        private long count;
    }

    @RestController
    @AllArgsConstructor
    @Tag(name = "stats", description = "API for stats")
    public static class GetNumberOfPostCreatedController extends AbstractController {

        private GetNumberOfPostCreatedService service;

        @Operation(
                summary = "Get number of post created in a time range."
        )
        @GetMapping("/stats/number-of-post")
        public ResponseEntity<Object> get(@ParameterObject GetNumberOfPostCreatedRequest request) {
            GetNumberOfPostCreatedResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class GetNumberOfPostCreatedService {

        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        public GetNumberOfPostCreatedResponse get(GetNumberOfPostCreatedRequest request) {
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

            final QPost qPost = QPost.post;

            BooleanExpression predicates = qPost.createdAt.between(start, end);

            final long count = new BlazeJPAQuery<Post>(entityManager, criteriaBuilderFactory)
                    .select(qPost)
                    .from(qPost)
                    .where(predicates)
                    .fetchCount();

            GetNumberOfPostCreatedResponse response = new GetNumberOfPostCreatedResponse(count);
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
