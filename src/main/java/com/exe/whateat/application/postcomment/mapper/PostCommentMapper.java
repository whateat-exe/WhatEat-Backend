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
                .accountId(postComment.getAccount().getId().asTsid())
                .fullName(postComment.getAccount().getFullName())
                .createdAt(postComment.getCreatedAt())
                .imageUrl(postComment.getAccount().getImage())
                .isModified(postComment.getVersion() > 0)
                .content(postComment.getContent())
                .build();
    }
}
