package com.exe.whateat.application.tag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.application.tag.response.TagResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.Tag;
import com.exe.whateat.entity.food.TagType;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateTag {

    @Getter
    @Setter
    public static final class CreateTagRequest {

        @NotNull(message = "tag name is required")
        private String tagName;

        @NotNull(message = "tag type is required")
        private String tagType;
    }

    @AllArgsConstructor
    @RestController
    @io.swagger.v3.oas.annotations.tags.Tag(
            name = "tags",
            description = "APIs for tags"
    )
    public static final class CreateTagController extends AbstractController {

        private CreateTagService createTagService;

        @PostMapping("/tags")
        @Operation(
                summary = "Create tag API. Returns the new information of tag.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the tag.",
                        content = @Content(schema = @Schema(implementation = CreateTag.CreateTagRequest.class))
                )
        )
        @ApiResponse(
                description = "Successful creation. Returns new information of the tag.",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = TagResponse.class))
        )
        @ApiResponse(
                description = "Failed creation of the tag.",
                responseCode = "400s/500s",
                content = @Content(schema = @Schema(implementation = WhatEatErrorResponse.class))
        )
        public ResponseEntity<Object> createTag(@RequestBody @Valid CreateTagRequest createTagRequest) {
            var response = createTagService.createTag(createTagRequest);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreateTagService {

        private TagRepository tagRepository;
        private TagMapper tagMapper;

        public TagResponse createTag(CreateTagRequest createTagRequest) {

            //check duplicate tag name
            Optional<Tag> tagCheckDuplicate = tagRepository.findByName(createTagRequest.tagName);
            if (tagCheckDuplicate.isPresent()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0004)
                        .reason("Tag bị trùng", "Tag tạo mới đã bị trùng tên")
                        .build();
            }

            if (!EnumUtils.isValidEnum(TagType.class, createTagRequest.tagType)) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0008)
                        .reason("Tag Type", "Thể loại tag không phù hợp")
                        .build();
            }

            Tag tag = Tag
                    .builder()
                    .id(WhatEatId.generate())
                    .name(createTagRequest.tagName)
                    .type(TagType.valueOf(createTagRequest.tagType))
                    .build();
            Tag tagCreated = tagRepository.saveAndFlush(tag);
            return tagMapper.convertToDto(tagCreated);
        }
    }
}
