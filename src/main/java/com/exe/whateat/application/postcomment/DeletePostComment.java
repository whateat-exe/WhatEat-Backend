package com.exe.whateat.application.postcomment;

import com.exe.whateat.application.common.AbstractController;
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
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

public class DeletePostComment {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_comment",
            description = "APIs for post."
    )
    public static final class DeletePostCommentController extends AbstractController {

        private final DeletePostCommentService service;

        @DeleteMapping("/posts/comments/{commentId}")
        @Operation(
                summary = "Delete post comment API"
        )
        @ApiResponse(
                description = "Successful. Returns the post comment.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed getting of the post comment.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> deletePostComment(@PathVariable Tsid commentId) {
            service.deletePost(commentId);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class DeletePostCommentService {

        private final PostCommentRepository postCommentRepository;

        public void deletePost(Tsid commentId) {
            final WhatEatId whatEatId = new WhatEatId(commentId);
            postCommentRepository.deleteById(whatEatId);
        }
    }
}
