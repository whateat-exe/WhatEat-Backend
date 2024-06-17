package com.exe.whateat.application.postcomment.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.postcomment.response.PostCommentResponse;
import com.exe.whateat.entity.post.PostComment;
import org.springframework.stereotype.Component;

@Component
public class PostCommentMapper implements WhatEatMapper<PostComment, PostCommentResponse> {
    @Override
    public PostCommentResponse convertToDto(PostComment postComment) {
        if (postComment == null) {
            return null;
        }
        return PostCommentResponse.builder()
                .id(postComment.getId().asTsid())
                .content(postComment.getContent())
                .build();
    }
}
