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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateTag {

    @Getter
    @Setter
    public static final class CreateTagRequest {

        @NotNull(message = "Tên của nhãn món ăn là cần thiết.")
        private String name;

        @NotNull(message = "Loại của nhãn món ăn là cần thiết.")
        private TagType type;
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
                summary = "Create tag API. Returns the new information of tag. Only for ADMIN and MANAGER.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Information of the tag.",
                        content = @Content(schema = @Schema(implementation = CreateTagRequest.class))
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
        public ResponseEntity<Object> createTag(@RequestBody @Valid CreateTagRequest request) {
            var response = createTagService.createTag(request);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @Transactional(rollbackOn = Exception.class)
    @AllArgsConstructor
    public static class CreateTagService {

        private TagRepository tagRepository;
        private TagMapper tagMapper;

        public TagResponse createTag(CreateTagRequest request) {
            if (tagRepository.existsByNameIgnoreCase(request.getName())) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0004)
                        .reason("name", "Nhãn món ăn tạo mới đã bị trùng tên")
                        .build();
            }
            Tag tag = Tag.builder()
                    .id(WhatEatId.generate())
                    .name(request.getName())
                    .type(request.getType())
                    .build();
            tag = tagRepository.save(tag);
            return tagMapper.convertToDto(tag);
        }
    }
}
