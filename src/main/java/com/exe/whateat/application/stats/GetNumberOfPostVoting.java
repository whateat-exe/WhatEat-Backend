package com.exe.whateat.application.stats;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
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
public final class GetNumberOfPostVoting {

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfPostVotingRequest {

        @Schema(type = "string", example = "01-01-2024")
        private String start;
        @Schema(type = "string", example = "01-01-2024")
        private String end;

    }

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfPostVotingResponse {
        private long count;
    }

    @RestController
    @AllArgsConstructor
    @Tag(name = "stats", description = "API for stats")
    public static class GetNumberOfPostVotingController extends AbstractController {

        private GetNumberOfPostVotingService service;

        @Operation(
                summary = "Get number of post voting in a time range."
        )
        @GetMapping("/stats/number-of-post-voting")
        public ResponseEntity<Object> get(@ParameterObject GetNumberOfPostVotingRequest request) {
            GetNumberOfPostVotingResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    public static class GetNumberOfPostVotingService {

        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final PostVotingRepository postVotingRepository;

        @Value("${whateat.tsid.epoch}")
        private long epoch;

        public GetNumberOfPostVotingService(EntityManager entityManager, CriteriaBuilderFactory criteriaBuilderFactory, PostVotingRepository postVotingRepository) {
            this.entityManager = entityManager;
            this.criteriaBuilderFactory = criteriaBuilderFactory;
            this.postVotingRepository = postVotingRepository;
        }

        public GetNumberOfPostVotingResponse get(GetNumberOfPostVotingRequest request) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            validateDateFormat(request.start);
            validateDateFormat(request.end);

            LocalDate startDate = LocalDate.parse(request.start, formatter);
            LocalDate endDate = LocalDate.parse(request.end, formatter);

            Instant start = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant end = endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();

            if (start.isAfter(end)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0024)
                        .reason("stats", "Ngày bắt đầu lớn hơn ngày kết thúc")
                        .build();
            }

            GetNumberOfPostVotingResponse response = new GetNumberOfPostVotingResponse(postVotingRepository.countRecordsInRange(start, end, epoch));
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
