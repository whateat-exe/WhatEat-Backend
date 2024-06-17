package com.exe.whateat.application.postcomment;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.postcomment.mapper.PostCommentMapper;
import com.exe.whateat.application.postcomment.response.PostCommentResponse;
import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostCommentRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdatePostComment {

    @Data
    @Builder
    public static final class UpdatePostCommentRequest {

        @NotNull(message = "Nội dung thay đổi cần có.")
        private String content;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_comment",
            description = "APIs for post voting."
    )
    public static final class UpdatePostVotingController extends AbstractController {

        private final UpdatePostCommentService service;

        @PutMapping("/posts/comments/{commentId}")
        @Operation(
                summary = "Create post comment.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the post comment.",
                        content = @Content(schema = @Schema(implementation = UpdatePostCommentRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns comment",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostVotingResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the post voting.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> updatePostComment(@RequestBody @Valid UpdatePostCommentRequest request, @PathVariable Tsid id) {
            final PostCommentResponse response = service.updatePostComment(request, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class UpdatePostCommentService {

        private final PostCommentRepository postCommentRepository;
        private final PostCommentMapper postCommentMapper;

        public PostCommentResponse updatePostComment(UpdatePostCommentRequest request, Tsid id) {
            final WhatEatId postCommentId = new WhatEatId(id);
            var postCommentExist = postCommentRepository.findById(postCommentId);
            if (postCommentExist.isPresent()) {
                postCommentExist.get().setContent(request.content);
                return postCommentMapper.convertToDto(postCommentRepository.saveAndFlush(postCommentExist.get()));
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("post_comment", "post comment này chưa có")
                    .build();
        }
    }
}
