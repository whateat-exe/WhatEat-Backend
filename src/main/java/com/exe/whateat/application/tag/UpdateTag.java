package com.exe.whateat.application.tag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.application.tag.response.TagResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.TagType;
import com.exe.whateat.infrastructure.exception.WhatEatErrorResponse;
import com.exe.whateat.infrastructure.repository.TagRepository;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateTag {

    @Getter
    @Setter
    public static class UpdateTagRequest {

        private String name;
        private TagType type;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "tags",
            description = "APIs for tags"
    )
    public static final class UpdateTagController extends AbstractController {

        private UpdateTagService updateTagService;

        @PatchMapping("/tags/{id}")
        @Operation(
                summary = "Update a tag through its ID API. Returns the information of the tag. Only for ADMIN and MANAGER."
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
        public ResponseEntity<Object> updateTag(@RequestBody UpdateTagRequest updateTagRequest, @PathVariable Tsid id) {
            var response = updateTagService.updateTag(updateTagRequest, id);
            return ResponseEntity.ok(response);
        }
    }

    @Service
    @AllArgsConstructor
    @Transactional(rollbackOn = Exception.class)
    public static class UpdateTagService {

        private final TagRepository tagRepository;
        private final TagMapper tagMapper;

        public TagResponse updateTag(UpdateTagRequest request, Tsid id) {
            var tag = tagRepository.findById(new WhatEatId(id))
                    .orElseThrow(() -> WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0010)
                            .reason("id", "Nhãn món ăn không tồn tại.")
                            .build());
            if (StringUtils.isNotBlank(request.getName()) && !Objects.equals(tag.getName(), request.getName())) {
                if (tagRepository.existsByNameIgnoreCase(request.getName())) {
                    throw WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0004)
                            .reason("name", "Nhãn món ăn tạo mới đã bị trùng tên.")
                            .build();
                }
                tag.setName(request.getName());
            }
            if (request.getType() != null) {
                tag.setType(request.getType());
            }
            var tagUpdated = tagRepository.save(tag);
            return tagMapper.convertToDto(tagUpdated);
        }
    }
}
