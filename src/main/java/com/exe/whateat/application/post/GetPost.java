package com.exe.whateat.application.post;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.post.mapper.PostMapper;
import com.exe.whateat.application.post.response.PostResponse;
import com.exe.whateat.application.postvoting.mapper.PostVotingMapper;
import com.exe.whateat.entity.common.PostVotingType;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.post.QPost;
import com.exe.whateat.entity.post.QPostComment;
import com.exe.whateat.entity.post.QPostVoting;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
public final class GetPost {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post",
            description = "APIs for post."
    )
    public static final class GetPostController extends AbstractController {

        private final GetPostService service;

        @GetMapping("/posts/{id}")
        @Operation(
                summary = "Get post API. Returns the post from the ID."
        )
        @ApiResponse(
                description = "Successful. Returns the post.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostResponse.class))
        )
        @ApiResponse(
                description = "Failed getting of the post.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getPost(@PathVariable Tsid id) {
            final PostResponse response = service.getPost(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class GetPostService {

        private final PostMapper postMapper;
        private final EntityManager entityManager;
        private final CriteriaBuilderFactory criteriaBuilderFactory;
        private final WhatEatSecurityHelper securityHelper;
        private final PostVotingRepository postVotingRepository;
        private final PostVotingMapper postVotingMapper;

        public PostResponse getPost(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            final QPost qPost = QPost.post;
            final QPostVoting qPostVoting = QPostVoting.postVoting;
            final QPostComment qPostComment = QPostComment.postComment;
            BooleanExpression predicates = qPost.isNotNull();
            predicates = predicates.and(qPost.id.eq(whatEatId));
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
                                    .where(qPostComment.post.eq(qPost))
                    )
                    .from(qPost)
                    .leftJoin(qPost.postImages).fetchJoin()
                    .leftJoin(qPost.account).fetchJoin()
                    .where(predicates)
                    .fetch();
            Tuple tuple = results.get(0);
            Post post = tuple.get(qPost);
            Long numberOfUp = tuple.get(1, Long.class);
            Long numberOfDown = tuple.get(2, Long.class);
            Long totalComments = tuple.get(3, Long.class);
            Long totalVotes = numberOfUp + numberOfDown;
            var postResponse =  postMapper.convertToDtoWithVoting(post, numberOfUp.intValue(), numberOfDown.intValue());
            setPostResponse(postResponse, post);
            postResponse.setTotalComment(totalComments);
            postResponse.setTotalVote(totalVotes);
            return postResponse;
        }

        private void setPostResponse (PostResponse postResponse, Post post) {
            var user = securityHelper.getCurrentLoggedInAccount()
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("account", "account chưa xác thực")
                            .build());
            var postVoting = postVotingRepository.postVotingAlreadyExists(user.getId(), post.getId());
            if(postVoting.isPresent()) {
                postResponse.setVoted(true);
                postResponse.setPostVoting(postVotingMapper.convertToDto(postVoting.get()));
            } else {
                postResponse.setVoted(false);
            }
        }
    }
}
