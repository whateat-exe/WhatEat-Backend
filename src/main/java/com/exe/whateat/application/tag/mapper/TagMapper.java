package com.exe.whateat.application.tag.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.tag.response.TagResponse;
import com.exe.whateat.entity.food.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper implements WhatEatMapper<Tag, TagResponse> {

    @Override
    public TagResponse convertToDto(Tag tag) {
        if (tag == null) {
            return null;
        }
        return TagResponse.builder()
                .id(tag.getId().asTsid())
                .name(tag.getName())
                .type(tag.getType().name())
                .build();
    }
}
