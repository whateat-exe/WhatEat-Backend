package com.exe.whateat.application.postcomment.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.postcomment.response.PostCommentResponse;
import com.exe.whateat.entity.post.PostComment;
import com.exe.whateat.infrastructure.format.InstantConverter;
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
                .createdAt(InstantConverter.convertInstantFormat(postComment.getCreatedAt()))
                .imageUrl(postComment.getAccount().getImage())
                .isModified(postComment.getVersion() > 0 ? true : false)
                .content(postComment.getContent())
                .build();
    }
}
