package com.exe.whateat.application.food;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.foodtag.response.FoodTagResponse;
import com.exe.whateat.application.foodtag.response.FoodTagsResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QFoodTag;
import com.exe.whateat.entity.food.TagType;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.github.x4ala1c.tsid.Tsid;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetTagsOfFood {

    @Getter
    @Setter
    public static final class GetTagsOfFoodRequest extends PaginationRequest {

        private String name;
        private TagType type;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "food",
            description = "APIs for food."
    )
    public static final class GetTagsOfFoodController extends AbstractController {

        private final GetTagsOfFoodService service;

        @GetMapping("/foods/{id}/tags")
        @Operation(
                summary = "Get tags of a food API. Returns the list of tags on said food paginated. ADMIN & MANAGER will return all."
        )
        @ApiResponse(
                description = "Successful. Returns list of the foods.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the foods.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getTagsOfFood(@PathVariable Tsid id, @ParameterObject GetTagsOfFoodRequest request) {
            final FoodTagsResponse response = service.get(id, request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    public static final class GetTagsOfFoodService {

        @PersistenceContext
        private EntityManager entityManager;

        private final WhatEatMapper<FoodTag, FoodTagResponse> mapper;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final FoodRepository foodRepository;

        @Autowired
        public GetTagsOfFoodService(WhatEatMapper<FoodTag, FoodTagResponse> mapper,
                                    CriteriaBuilderFactory criteriaBuilderFactory,
                                    FoodRepository foodRepository) {
            this.mapper = mapper;
            this.criteriaBuilderFactory = criteriaBuilderFactory;
            this.foodRepository = foodRepository;
        }

        @SuppressWarnings("Duplicates")
        public FoodTagsResponse get(Tsid id, GetTagsOfFoodRequest request) {
            final WhatEatId foodId = new WhatEatId(id);
            if (!foodRepository.existsById(foodId)) {
                throw WhatEatException.builder()
                        .code(WhatEatErrorCode.WEB_0005)
                        .reason("food", String.format("Món ăn với ID '%s' không tồn tại.", id))
                        .build();
            }
            final QFoodTag qFoodTag = QFoodTag.foodTag;
            BooleanExpression predicates = qFoodTag.isNotNull();
            predicates = predicates.and(qFoodTag.food.id.eq(foodId));
            if (StringUtils.isNotBlank(request.getName())) {
                predicates = predicates.and(qFoodTag.tag.name.containsIgnoreCase(request.getName()));
            }
            if (request.getType() != null) {
                predicates = predicates.and(qFoodTag.tag.type.eq(request.getType()));
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
            foodTags.forEach(ft -> ft.setFood(null));
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
