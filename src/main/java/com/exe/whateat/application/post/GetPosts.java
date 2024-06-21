package com.exe.whateat.application.post;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.request.PaginationRequest;
import com.exe.whateat.application.post.mapper.PostImageMapper;
import com.exe.whateat.application.post.mapper.PostMapper;
import com.exe.whateat.application.post.response.PostResponse;
import com.exe.whateat.application.post.response.PostsResponse;
import com.exe.whateat.entity.common.PostVotingType;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.post.QPost;
import com.exe.whateat.entity.post.QPostComment;
import com.exe.whateat.entity.post.QPostVoting;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetPosts {

    @Getter
    @Setter
    public static final class GetPostsRequest extends PaginationRequest {
    }

    @AllArgsConstructor
    @RestController
    @Tag(
            name = "post",
            description = "APIs for posts."
    )
    public static final class GetPostsController extends AbstractController {

        private final GetPostsService getPostsService;

        @Operation(
                summary = "Get posts API. Returns the new information of posts."
        )
        @ApiResponse(
                description = "Successful update. Returns new information of posts.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostsResponse.class))
        )
        @ApiResponse(
                description = "Failed updating of the post.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        @GetMapping("/posts")
        public ResponseEntity<Object> getPosts(@ParameterObject GetPostsRequest getPostsRequest) {
            var response = getPostsService.getPosts(getPostsRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class GetPostsService {

        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final PostMapper postMapper;
        private final PostImageMapper postImageMapper;
        private final WhatEatSecurityHelper securityHelper;
        private final PostVotingRepository postVotingRepository;

        public PostsResponse getPosts(GetPostsRequest getPostsRequest) {
            final QPost qPost = QPost.post;
            final QPostVoting qPostVoting = QPostVoting.postVoting;
            final QPostComment qPostComment = QPostComment.postComment;
            BlazeJPAQuery<Post> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory);
            final List<Tuple> results = query
                    .select(qPost,
                            JPAExpressions.select(qPostVoting.count())
                                    .from(qPostVoting)
                                    .where(qPostVoting.post.eq(qPost)
                                            .and(qPostVoting.type.eq(PostVotingType.UP))),
                            JPAExpressions.select(qPostVoting.count())
                                    .from(qPostVoting)
                                    .where(qPostVoting.post.eq(qPost)
                                            .and(qPostVoting.type.eq(PostVotingType.DOWN))),
                            JPAExpressions.select(qPostComment.count())
                                    .from(qPostComment)
                                    .where(qPostComment.post.eq(qPost)
                            ))
                    .from(qPost)
                    .leftJoin(qPost.postImages).fetchJoin()
                    .leftJoin(qPost.account).fetchJoin()
                    .limit(getPostsRequest.getLimit())
                    .offset(getPostsRequest.getOffset())
                    .fetch();
            final long count = new BlazeJPAQuery<Post>(entityManager, criteriaBuilderFactory)
                    .select(qPost)
                    .from(qPost)
                    .fetchCount();
            List<PostResponse> postResponses = new ArrayList<>();
            Set<Long> seenPostIds = new HashSet<>();
            for (Tuple tuple : results) {
                Post post = tuple.get(qPost);
                Long numberOfUp = tuple.get(1, Long.class);
                Long numberOfDown = tuple.get(2, Long.class);
                Long totalComments = tuple.get(3, Long.class);
                Long totalVotes = numberOfUp + numberOfDown;
                // Check if we have already processed this post ID
                if (seenPostIds.contains(post.getId().asTsid().asLong())) {
                    continue;
                }

                // Add the post ID to seen IDs
                seenPostIds.add(post.getId().asTsid().asLong());

                var postResponse = postMapper.convertToDtoWithVoting(post, numberOfUp.intValue(), numberOfDown.intValue());
                setPostResponse(postResponse, post);
                postResponse.setTotalComment(totalComments);
                postResponse.setTotalVote(totalVotes);
                postResponses.add(postResponse);
            }
            PostsResponse response = new PostsResponse(postResponses, count);
            response.setLimit(getPostsRequest.getLimit());
            response.setPage(getPostsRequest.getPage());
            return response;
        }
        private void setPostResponse (PostResponse postResponse, Post post) {
            var user = securityHelper.getCurrentLoggedInAccount();
            var postVoting = postVotingRepository.postVotingAlreadyExists(user.get().getId(), post.getId());
            if(postVoting.isPresent()) {
                postResponse.setVoted(true);
            }
            else {
                postResponse.setVoted(false);
            }

            if (post.getVersion() > 0) {
                postResponse.setModified(true);
            }
            else {
                postResponse.setModified(false);
            }
        }
    }
}
