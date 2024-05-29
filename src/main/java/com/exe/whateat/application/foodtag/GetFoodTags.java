package com.exe.whateat.application.foodtag;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
import com.exe.whateat.application.foodtag.response.FoodTagsResponse;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QFoodTag;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetFoodTags {

    @Getter
    @Setter
    public static final class GetFoodTagsRequest extends PaginationRequest {

        private String name;
        private ActiveStatus status;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtags",
            description = "APIs for food tags."
    )
    public static final class GetFoodTagsController extends AbstractController {

        private final GetFoodTagsService service;

        @GetMapping("/foodtags")
        @Operation(
                summary = "Get list of food tag API. Returns the paginated list of food tag. Only for ADMIN and MANAGER."
        )
        @ApiResponse(
                description = "Successful. Returns the list.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodTagsResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the food tags.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getFoodTags(@ParameterObject GetFoodTagsRequest request) {
            final FoodTagsResponse response = service.get(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    public static final class GetFoodTagsService {

        @PersistenceContext
        private EntityManager entityManager;

        private final WhatEatMapper<FoodTag, FoodTagResponse> mapper;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        @Autowired
        public GetFoodTagsService(WhatEatMapper<FoodTag, FoodTagResponse> mapper, CriteriaBuilderFactory criteriaBuilderFactory) {
            this.mapper = mapper;
            this.criteriaBuilderFactory = criteriaBuilderFactory;
        }

        @SuppressWarnings("Duplicates")
        public FoodTagsResponse get(GetFoodTagsRequest request) {
            final QFoodTag qFoodTag = QFoodTag.foodTag;
            BooleanExpression predicates = qFoodTag.isNotNull();
            if (StringUtils.isNotBlank(request.getName())) {
                predicates = predicates.and(qFoodTag.food.name.containsIgnoreCase(request.getName())
                        .or(qFoodTag.tag.name.containsIgnoreCase(request.getName())));
            }
            if (request.getStatus() != null) {
                predicates = predicates.and(qFoodTag.status.eq(request.getStatus()));
            }
            BlazeJPAQuery<FoodTag> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory);
            final List<FoodTag> foodTags = query
                    .select(qFoodTag)
                    .from(qFoodTag)
                    .leftJoin(qFoodTag.food).fetchJoin()
                    .leftJoin(qFoodTag.tag).fetchJoin()
                    .where(predicates)
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .fetch();
            final long count = new BlazeJPAQuery<FoodTag>(entityManager, criteriaBuilderFactory)
                    .select(qFoodTag)
                    .from(qFoodTag)
                    .where(predicates)
                    .fetchCount();
            final FoodTagsResponse response = new FoodTagsResponse(foodTags.stream().map(mapper::convertToDto).toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }
    }
}
