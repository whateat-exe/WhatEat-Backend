package com.exe.whateat.application.post;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DeletePost {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post",
            description = "APIs for post."
    )
    public static final class DeletePostController extends AbstractController {

        private final DeletePostService service;

        @DeleteMapping("/posts/{id}")
        @Operation(
                summary = "Delete post API"
        )
        @ApiResponse(
                description = "Successful. Returns the post.",
                responseCode = "204"
        )
        @ApiResponse(
                description = "Failed getting of the post.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> deleteFood(@PathVariable Tsid id) {
            service.deletePost(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class DeletePostService {

        private final PostRepository postRepository;

        public void deletePost(Tsid id) {
            final WhatEatId whatEatId = new WhatEatId(id);
            postRepository.deleteById(whatEatId);
        }
    }
}