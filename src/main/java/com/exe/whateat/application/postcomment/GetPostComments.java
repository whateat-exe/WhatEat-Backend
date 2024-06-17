package com.exe.whateat.application.postcomment;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.dish.GetDishes;
import com.exe.whateat.application.dish.mapper.DishMapper;
import com.exe.whateat.application.dish.response.DishesResponse;
import com.exe.whateat.application.postcomment.mapper.PostCommentMapper;
import com.exe.whateat.application.postcomment.response.PostCommentsResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Dish;
import com.exe.whateat.entity.food.FoodTag;
import com.exe.whateat.entity.food.QDish;
import com.exe.whateat.entity.post.PostComment;
import com.exe.whateat.entity.post.QPost;
import com.exe.whateat.entity.post.QPostComment;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

public class GetPostComments {

    @Data
    private static final class GetPostCommentsRequest extends PaginationRequest {

    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_comment",
            description = "get post_comments"
    )
    public static class GetDishesController extends AbstractController {

        private GetPostCommentsService service;

        @Operation(
                summary = "Get post comments API"
        )
        @ApiResponse(
                description = "Successful. Returns list of the dishes.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostCommentsResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the post comments.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/posts/{id}/comments")
        public ResponseEntity<Object> getDishes(@Valid GetPostCommentsRequest getPostCommentsRequest, @PathVariable Tsid id) {
            var response = service.getPostComments(getPostCommentsRequest, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetPostCommentsService {

        private EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final PostCommentMapper postCommentMapper;

        public PostCommentsResponse getPostComments(GetPostCommentsRequest request, Tsid postId) {
            final QPostComment qPostComment = QPostComment.postComment;
            BlazeJPAQuery<Dish> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory);
            BooleanExpression predicates = qPostComment.isNotNull();
            predicates = predicates.and(qPostComment.post.id.eq(WhatEatId.builder().id(postId).build()));
            final List<PostComment> postComments = query
                    .select(qPostComment)
                    .from(qPostComment)
                    .where(predicates)
                    .limit(request.getLimit())
                    .offset(request.getOffset())
                    .fetch();
            final long count = new BlazeJPAQuery<PostComment>(entityManager, criteriaBuilderFactory)
                    .select(qPostComment)
                    .from(qPostComment)
                    .fetchCount();
            final PostCommentsResponse response = new PostCommentsResponse(postComments.stream().map(postCommentMapper::convertToDto).toList(), count);
            response.setLimit(request.getLimit());
            response.setPage(request.getPage());
            return response;
        }
    }
}
