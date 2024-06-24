package com.exe.whateat.application.postcomment;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.postcomment.mapper.PostCommentMapper;
import com.exe.whateat.application.postcomment.response.PostCommentResponse;
import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.PostComment;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostCommentRepository;
import com.exe.whateat.infrastructure.repository.PostRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

public class CreatePostComment {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CreatePostCommentRequest {

        @NotNull(message = "Nội dung tạo comment cần có.")
        private String content;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_comment",
            description = "APIs for post comment."
    )
    public static final class UpdatePostCommentController extends AbstractController {

        private final CreatePostCommentService service;

        @PostMapping("/posts/{id}/comments")
        @Operation(
                summary = "Create post comment.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the post comment.",
                        content = @Content(schema = @Schema(implementation = CreatePostCommentRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns comment",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostVotingResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the post comment.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> updatePostComment(@RequestBody @Valid CreatePostCommentRequest request, @PathVariable Tsid id) {
            final PostCommentResponse response = service.createPostComment(request, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreatePostCommentService {

        private final PostCommentRepository postCommentRepository;
        private final PostCommentMapper postCommentMapper;
        private final PostRepository postRepository;
        private final WhatEatSecurityHelper securityHelper;

        public PostCommentResponse createPostComment(CreatePostCommentRequest request, Tsid id) {
            final WhatEatId postId = new WhatEatId(id);
            var user = securityHelper.getCurrentLoggedInAccount();
            if (user.isPresent()) {
                PostComment postComment = PostComment.builder()
                        .id(WhatEatId.generate())
                        .content(request.content)
                        .account(user.get())
                        .post(postRepository.getReferenceById(postId))
                        .createdAt(Instant.now())
                        .lastModified(Instant.now())
                        .build();
                return postCommentMapper.convertToDto(postCommentRepository.save(postComment));
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("post_comment", "Lỗi tạo comment vì chưa login")
                    .build();
        }
    }
}
