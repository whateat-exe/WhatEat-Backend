package com.exe.whateat.application.postvoting;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.postvoting.mapper.PostVotingMapper;
import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.exe.whateat.entity.common.PostVotingType;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.PostVoting;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.AccountRepository;
import com.exe.whateat.infrastructure.repository.PostRepository;
import com.exe.whateat.infrastructure.repository.PostVotingRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

public class UpdatePostVoting {

    @Data
    @Builder
    public static final class UpdatePostVotingRequest {

        @NotNull(message = "Loại của voting phải có.")
        private PostVotingType type;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "post_voting",
            description = "APIs for post voting."
    )
    public static final class UpdatePostVotingController extends AbstractController {

        private final UpdatePostVotingService service;

        @PutMapping("/posts/post_voting/{id}")
        @Operation(
                summary = "Create post voting.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the post voting.",
                        content = @Content(schema = @Schema(implementation = UpdatePostVotingRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns number of voting for each",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = FoodResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the post voting.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> updatePostVoting(@RequestBody @Valid UpdatePostVotingRequest request, @PathVariable Tsid id) {
            final PostVotingResponse response = service.updatePostVoting(request, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class UpdatePostVotingService {

        private final PostVotingRepository postVotingRepository;
        private final PostVotingMapper postVotingMapper;

        public PostVotingResponse updatePostVoting(UpdatePostVotingRequest request, Tsid id) {
            final WhatEatId postVotingId = new WhatEatId(id);
            var postVotingExist = postVotingRepository.findById(postVotingId);
            if (postVotingExist != null) {
                postVotingExist.get().setType(request.type);
                return postVotingMapper.convertToDto(postVotingRepository.saveAndFlush(postVotingExist.get()));
            }
            throw WhatEatException
                    .builder()
                    .code(WhatEatErrorCode.WES_0001)
                    .reason("postvoting", "post voting này chưa có")
                    .build();
        }
    }
}
