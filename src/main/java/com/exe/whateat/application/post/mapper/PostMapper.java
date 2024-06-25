package com.exe.whateat.application.post.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.post.response.PostResponse;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Builder
@Component
public class PostMapper implements WhatEatMapper<Post, PostResponse> {

    private AccountDTOMapper accountDTOMapper;
    private PostImageMapper postImageMapper;

    public PostResponse convertToDtoWithVoting(Post post, int numberOfVotingUp, int numberOfVotingDown) {
        if (post == null) {
            return null;
        }
        return PostResponse.builder()
                .id(post.getId().asTsid())
                .accountName(post.getAccount().getFullName())
                .content(post.getContent())
                .postImages(post.getPostImages().stream().map(postImageMapper::convertToDto).toList())
                .numberOfUp(numberOfVotingUp)
                .numberOfDown(numberOfVotingDown)
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Override
    public PostResponse convertToDto(Post post) {
        if (post == null) {
            return null;
        }
        return PostResponse.builder()
                .id(post.getId().asTsid())
                .accountName(post.getAccount().getFullName())
                .content(post.getContent())
                .postImages(post.getPostImages().stream().map(postImageMapper::convertToDto).toList())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
