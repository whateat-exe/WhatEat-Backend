package com.exe.whateat.application.foodtag;

import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.application.tag.response.TagsResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QFoodTag;
import com.exe.whateat.entity.food.QTag;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodTagRepository;
import com.querydsl.jpa.impl.JPAQuery;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetTagsByFood {

    public static final class GetFoodTagsRequest extends PaginationRequest {

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "foodtag",
            description = "get a foodtag by food id"
    )
    public static final class GetFoodTagsByFoodController {

        private GetTagsByFoodService getTagsByFoodService;

        @GetMapping("/foodtags/food/{id}")
        @Operation(
                summary = "Get tags API. Returns the tags from the food ID."
        )
        @ApiResponse(
                description = "Successful. Returns the tags. ADMIN & MANAGER will return any status",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = TagsResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the food tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getTagsByFood(@PathVariable Tsid id, @Valid GetFoodTagsRequest request) {
            var response = getTagsByFoodService.getTagsByFood(id, request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetTagsByFoodService {

        private FoodTagRepository foodTagRepository;
        private TagMapper tagMapper;

        @PersistenceContext
        private EntityManager entityManager;

        public TagsResponse getTagsByFood(Tsid tsid, GetFoodTagsRequest getFoodTagsRequest) {
            final QFoodTag qFoodTag = QFoodTag.foodTag;
            final QTag qTag = QTag.tag;
            final JPAQuery<FoodTag> foodTagJPAQuery = new JPAQuery<>(entityManager)
                    .select(qFoodTag)
                    .from(qFoodTag)
                    .leftJoin(qFoodTag.tag, qTag)
                    .where(qFoodTag.food.id.eq(WhatEatId.builder().id(tsid).build()))
                    .limit(getFoodTagsRequest.getLimit())
                    .offset(getFoodTagsRequest.getOffset());
            final List<FoodTag> foodTags = foodTagJPAQuery.fetch();
            final long total = foodTagRepository.count();
            final TagsResponse response = new TagsResponse(foodTags.stream().map(x -> tagMapper.convertToDto(x.getTag())).toList(), total);
            response.setPage(getFoodTagsRequest.getPage());
            response.setLimit(getFoodTagsRequest.getLimit());
            return response;
        }
    }
}
