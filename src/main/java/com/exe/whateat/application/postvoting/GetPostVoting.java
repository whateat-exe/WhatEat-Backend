package com.exe.whateat.application.postvoting;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.postvoting.mapper.PostVotingMapper;
import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GetPostVoting {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_voting",
            description = "APIs for post voting."
    )
    public static final class GetPostVotingController extends AbstractController {

        private final GetPostVotingService service;

        @GetMapping("/posts/{id}/post-votings")
        @Operation(
                summary = "Get post voting. An api for get whether user voted."
        )
        @ApiResponse(
                description = "Successful return voting of user has voted.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = PostVotingResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the post voting.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getPostVoting(@PathVariable Tsid id) {
            final PostVotingResponse response = service.get(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class GetPostVotingService {

        private PostVotingRepository postVotingRepository;
        private PostVotingMapper postVotingMapper;
        private WhatEatSecurityHelper securityHelper;

        public PostVotingResponse get(Tsid id) {
            final WhatEatId postId = new WhatEatId(id);
            var user = securityHelper.getCurrentLoggedInAccount();
            if (user.isPresent()) {
                var postVoting = postVotingRepository.postVotingAlreadyExists(user.get().getId(), postId);
                if (postVoting.isPresent()) {
                    return postVotingMapper.convertToDto(postVoting.get());
                }
                return null;
            }
            throw WhatEatException.builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("server", "Lỗi security")
                    .build();
        }
    }
}
