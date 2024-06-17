package com.exe.whateat.application.postvoting;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
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

public class DeletePostVoting {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_voting",
            description = "APIs for post voting."
    )
    public static final class CreatePostVotingController extends AbstractController {

        private final DeletePostVotingService service;

        @DeleteMapping("/posts/post-voting/{id}")
        @Operation(
                summary = "Delete post voting."
        )
        @ApiResponse(
                description = "Successful deleting",
                responseCode = "200"
        )
        @ApiResponse(
                description = "Failed creation of the post voting.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createFood(@PathVariable Tsid id) {
            service.deletePostVoting(id);
            return ResponseEntity.noContent().build();
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class DeletePostVotingService {

        private final PostVotingRepository postVotingRepository;

        public void deletePostVoting(Tsid id) {
            var postVoting = postVotingRepository.findById(WhatEatId.builder().id(id).build());
            if (postVoting.isPresent())
                postVotingRepository.delete(postVoting.get());
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("postvoting", "Không tìm thấy post voting")
                    .build();
        }
    }
}
