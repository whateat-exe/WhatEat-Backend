package com.exe.whateat.application.stats;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.infrastructure.repository.RandomHistoryRepository;
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetNumberOfRandom {

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfRandomRequest {

        @Schema(type = "string", example = "01-01-2024")
        private String start;
        @Schema(type = "string", example = "01-01-2024")
        private String end;

    }

    @Getter
    @Setter
    @Builder
    public static final class GetNumberOfRandomResponse {
        private long count;
    }

    @RestController
    @AllArgsConstructor
    @Tag(name = "stats", description = "API for stats")
    public static class GetNumberOfRandomController extends AbstractController {

        private GetNumberOfRandomService service;

        @Operation(
                summary = "Get number of random in a time range."
        )
        @GetMapping("/stats/number-of-random")
        public ResponseEntity<Object> get(@ParameterObject GetNumberOfRandomRequest request) {
            GetNumberOfRandomResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    public static class GetNumberOfRandomService {

        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final RandomHistoryRepository randomHistoryRepository;

        @Value("${whateat.tsid.epoch}")
        private long epoch;

        public GetNumberOfRandomService(EntityManager entityManager, CriteriaBuilderFactory criteriaBuilderFactory, RandomHistoryRepository randomHistoryRepository) {
            this.entityManager = entityManager;
            this.criteriaBuilderFactory = criteriaBuilderFactory;
            this.randomHistoryRepository = randomHistoryRepository;
        }

        public GetNumberOfRandomResponse get(GetNumberOfRandomRequest request) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            LocalDate startDate = LocalDate.parse(request.start, formatter);
            LocalDate endDate = LocalDate.parse(request.end, formatter);

            Instant start = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant end = endDate.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();

            GetNumberOfRandomResponse response = new GetNumberOfRandomResponse(randomHistoryRepository.countRecordsInRange(start, end, epoch));
            return response;
        }

    }

}
