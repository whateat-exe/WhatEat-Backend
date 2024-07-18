package com.exe.whateat.application.tag;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.application.tag.response.TagsResponse;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QTag;
import com.exe.whateat.entity.food.Tag;
import com.exe.whateat.entity.food.TagType;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.TagRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetTags {

    @Getter
    @Setter
    public static final class GetTagsRequest extends PaginationRequest {

        private String name;
        private TagType type;
    }

    @RestController
    @AllArgsConstructor
    @io.swagger.v3.oas.annotations.tags.Tag(
            name = "tags",
            description = "APIs for tags"
    )
    public static class GetTagController extends AbstractController {

        private final GetTagService getTagService;

        @GetMapping("/tags")
        @Operation(
                summary = "Get tags API. Returns the list of tags. Only for ADMIN and MANAGER.",
                parameters = {
                        @Parameter
                }
        )
        @ApiResponse(
                description = "Successfully found.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = TagsResponse.class))
        )
        @ApiResponse(
                description = "Failed returning of the tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getTagController(@ParameterObject GetTagsRequest getTagsRequest) {
            var response = getTagService.getTagService(getTagsRequest);
            return ResponseEntity.ok(response);
        }
    }

    @AllArgsConstructor
    @Service
    public static class GetTagService {

        private final TagRepository tagRepository;
        private final TagMapper tagMapper;
        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;

        public TagsResponse getTagService(GetTagsRequest request) {
            final QTag qTag = QTag.tag;
            BooleanExpression predicates = qTag.isNotNull();
            if (StringUtils.isNotBlank(request.getName())) {
                predicates = predicates.and(qTag.name.containsIgnoreCase(request.getName()));
            }
            if (request.getType() != null) {
                predicates = predicates.and(qTag.type.eq(request.getType()));
            }

            final JPAQuery<Tag> tagJPAQuery = new JPAQuery<>(entityManager)
                    .select(qTag)
                    .from(qTag)
                    .where(predicates)
                    .orderBy(qTag.priority.asc(), qTag.name.asc())
                    .limit(request.getLimit())
                    .offset(request.getOffset());

            final List<Tag> tags = tagJPAQuery.fetch();

            final long count = new BlazeJPAQuery<FoodTag>(entityManager, criteriaBuilderFactory)
                    .select(qTag)
                    .from(qTag)
                    .where(predicates)
                    .fetchCount();

            final TagsResponse response = new TagsResponse(tags.stream().map(tagMapper::convertToDto).toList(), count);
            response.setPage(request.getPage());
            response.setLimit(request.getLimit());
            return response;
        }
    }
}
