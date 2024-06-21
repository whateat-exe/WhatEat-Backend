package com.exe.whateat.application.post.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.post.response.PostImageResponse;
import com.exe.whateat.entity.post.PostImage;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
@Builder
public class PostImageMapper implements WhatEatMapper<PostImage, PostImageResponse> {

    @Override
    public PostImageResponse convertToDto(PostImage postImage) {
        if (postImage == null) {
            return null;
        }
        return PostImageResponse.builder()
                .id(postImage.getId().asTsid())
                .caption(postImage.getCaption())
                .imageUrl(postImage.getImage())
                .build();
    }
}
