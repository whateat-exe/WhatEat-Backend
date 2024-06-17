package com.exe.whateat.application.postvoting;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.post.mapper.PostMapper;
import com.exe.whateat.application.postvoting.mapper.PostVotingMapper;
import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.exe.whateat.entity.common.PostVotingType;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.PostVoting;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.repository.PostRepository;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class CreatePostVoting {

    @Data
    @Builder
    public static final class CreatePostVotingRequest {

        @NotNull(message = "Loại của voting phải có.")
        private PostVotingType type;

        @NotNull(message = "post id phải có")
        private Tsid postId;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_voting",
            description = "APIs for post voting."
    )
    public static final class CreatePostVotingController extends AbstractController {

        private final CreatePostVotingService service;

        @PostMapping("/posts/post-votings")
        @Operation(
                summary = "Create post voting.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the post voting.",
                        content = @Content(schema = @Schema(implementation = CreatePostVotingRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns number of voting for each",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostVotingResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the post voting.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createPostVoting(@RequestBody @Valid CreatePostVotingRequest request) {
            final PostVotingResponse response = service.createPostVoting(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreatePostVotingService {

        private final PostVotingRepository postVotingRepository;
        private final PostVotingMapper postVotingMapper;
        private final PostRepository postRepository;
        private final WhatEatSecurityHelper securityHelper;

        public PostVotingResponse createPostVoting(CreatePostVotingRequest request) {
            var user = securityHelper.getCurrentLoggedInAccount();
            final WhatEatId postId = new WhatEatId(request.postId);
            var postVotingExist = postVotingRepository.postVotingAlreadyExists(user.get().getId(), postId);
            if (postVotingExist.isPresent()) {
               throw WhatEatException
                       .builder()
                       .code(WhatEatErrorCode.WES_0001)
                       .reason("postvoting", "post voting này đã vote")
                       .build();
            }
            PostVoting postVoting = PostVoting
                    .builder()
                    .id(WhatEatId.generate())
                    .post(postRepository.getReferenceById(postId))
                    .account(user.get())
                    .type(request.type)
                    .build();
            return postVotingMapper.convertToDto(postVotingRepository.saveAndFlush(postVoting));
        }
    }
}
