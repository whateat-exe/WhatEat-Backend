package com.exe.whateat.application.postvoting;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.GetFood;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.postvoting.mapper.PostVotingMapper;
import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Food;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.FoodRepository;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
import com.exe.whateat.infrastructure.security.WhatEatSecurityHelper;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

public class GetPostVoting {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_voting",
            description = "APIs for post voting."
    )
    public static final class GetFoodController extends AbstractController {

        private final GetPostVotingService service;

        @GetMapping("/posts/{id}/post_voting")
        @Operation(
                summary = "Get post voting."
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
        public ResponseEntity<Object> getPostVoting(@PathVariable Tsid id) {
            final PostVotingResponse response = service.get(id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    public static final class GetPostVotingService {

        private PostVotingRepository postVotingRepository;
        private PostVotingMapper postVotingMapper;
        private WhatEatSecurityHelper securityHelper;

        public PostVotingResponse get(Tsid id) {
            final WhatEatId postId = new WhatEatId(id);
            var user = securityHelper.getCurrentLoggedInAccount();
            if (user.isPresent()) {
                var postVoting = postVotingRepository.postVotingAlreadyExists(postId, user.get().getId());
                if(postVoting.isPresent()) {
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
