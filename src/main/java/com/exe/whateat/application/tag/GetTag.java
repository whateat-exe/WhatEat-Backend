package com.exe.whateat.application.tag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.application.tag.response.TagResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.TagRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GetTag {

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "tag",
            description = "Get a tag by id"
    )
    public static final class GetTagController extends AbstractController {

        private final GetTagService getTagService;

        @GetMapping("tags/{id}")
        @Operation(
                summary = "Get a tag through its ID API. Returns the information of the tag. Only for ADMIN and Manager."
        )
        @ApiResponse(
                description = "Successfully found.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = TagResponse.class))
        )
        @ApiResponse(
                description = "Failed returning of the tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> getTag(@PathVariable Tsid id) {

            return ResponseEntity.ok(getTagService.getTag(id));
        }
    }

    @Service
    @AllArgsConstructor
    public static class GetTagService {

        private final TagRepository tagRepository;
        private final TagMapper tagMapper;

        public TagResponse getTag(Tsid tsid) {
            var tag = tagRepository.findById(WhatEatId.builder().id(tsid).build());
            if (tag.isEmpty()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("lỗi gửi id", "gửi id sai hoặc không đúng định dạng")
                        .build();
            }
            return tagMapper.convertToDto(tag.get());
        }
    }
}
