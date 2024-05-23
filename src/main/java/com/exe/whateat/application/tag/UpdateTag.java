package com.exe.whateat.application.tag;

import com.exe.whateat.application.common.AbstractController;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.tag.mapper.TagMapper;
import com.exe.whateat.application.tag.response.TagResponse;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.food.TagType;
import com.exe.whateat.infrastructure.repository.TagRepository;
import com.querydsl.core.util.StringUtils;
import io.github.x4ala1c.tsid.Tsid;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UpdateTag {

    @Getter
    @Setter
    public static class UpdateTagRequest {

        private String tagName;
        private String tagType;
    }

    @RestController
    @AllArgsConstructor
    @Tag(
            name = "tag",
            description = "update tag"
    )
    public static final class UpdateTagController extends AbstractController {

        private UpdateTagService updateTagService;

        @PatchMapping("tags/{id}")
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

        public TagResponse updateTag(UpdateTagRequest updateTagRequest, Tsid tsid) {

            var tag = tagRepository.findById(WhatEatId.builder().id(tsid).build());
            //Gửi tag id lỗi là lỗi server
            if (tag.isEmpty()) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WES_0001)
                        .reason("lỗi gửi id", "Gửi id sai hoặc không đúng định dạng")
                        .build();
            }

            // Check name update trung
            var getAllTag = tagRepository.findAll();
            if (!StringUtils.isNullOrEmpty(updateTagRequest.getTagName())) {
                Optional<com.exe.whateat.entity.food.Tag> optionalTag = getAllTag.stream().filter(x ->
                        x.getName().equalsIgnoreCase(updateTagRequest.tagName)
                                && Objects.equals(x.getId(), tag.get().getId())).findFirst();
                if (optionalTag.isPresent()) {
                    throw WhatEatException
                            .builder()
                            .code(WhatEatErrorCode.WEB_0004)
                            .reason("Tag bị trùng", "Tag tạo mới đã bị trùng tên")
                            .build();
                }
            }

            // Check type co chuan voi enum
            if (!StringUtils.isNullOrEmpty(updateTagRequest.tagType) && !EnumUtils.isValidEnum(TagType.class, updateTagRequest.tagType)) {
                throw WhatEatException
                        .builder()
                        .code(WhatEatErrorCode.WEB_0008)
                        .reason("tag không phù hợp", "Thể loại tag không có trong dữ liệu có sẵn")
                        .build();
            }

            if (!StringUtils.isNullOrEmpty(updateTagRequest.tagName)) {
                tag.get().setName(updateTagRequest.tagName);
            }

            if (!StringUtils.isNullOrEmpty(updateTagRequest.tagType)) {
                tag.get().setType(TagType.valueOf(updateTagRequest.tagType));
            }

            var tagUpdated = tagRepository.saveAndFlush(tag.get());
            return tagMapper.convertToDto(tagUpdated);
        }
    }
}
