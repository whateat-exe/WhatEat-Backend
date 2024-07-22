package com.exe.whateat.application.post.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.post.response.PostResponse;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.Post;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PostMapper implements WhatEatMapper<Post, PostResponse> {

    private PostImageMapper postImageMapper;
    private RestaurantRepository restaurantRepository;

    public PostResponse convertToDtoWithVoting(Post post, int numberOfVotingUp, int numberOfVotingDown) {
        if (post == null) {
            return null;
        }

        if (post.getAccount().getRole().equals(AccountRole.RESTAURANT)){
            WhatEatId whatEatId = post.getAccount().getId();
            Restaurant restaurant = restaurantRepository.findByAccountId(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("restaurant", "Lỗi gửi sai id của restaurant")
                            .build());
            return PostResponse.builder()
                    .id(post.getId().asTsid())
                    .accountName(restaurant.getName())
                    .avatarImage(restaurant.getImage())
                    .content(post.getContent())
                    .postImages(post.getPostImages().stream().map(postImageMapper::convertToDto).toList())
                    .numberOfUp(numberOfVotingUp)
                    .numberOfDown(numberOfVotingDown)
                    .lastModified(post.getLastModified())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
        return PostResponse.builder()
                .id(post.getId().asTsid())
                .accountName(post.getAccount().getFullName())
                .avatarImage(post.getAccount().getImage())
                .content(post.getContent())
                .postImages(post.getPostImages().stream().map(postImageMapper::convertToDto).toList())
                .numberOfUp(numberOfVotingUp)
                .numberOfDown(numberOfVotingDown)
                .lastModified(post.getLastModified())
                .createdAt(post.getCreatedAt())
                .build();
    }

    @Override
    public PostResponse convertToDto(Post post) {
        if (post == null) {
            return null;
        }

        if (post.getAccount().getRole().equals(AccountRole.RESTAURANT)){
            WhatEatId whatEatId = post.getAccount().getId();
            Restaurant restaurant = restaurantRepository.findByAccountId(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("restaurant", "Lỗi gửi sai id của restaurant")
                            .build());
            return PostResponse.builder()
                    .id(post.getId().asTsid())
                    .accountName(restaurant.getName())
                    .avatarImage(restaurant.getImage())
                    .content(post.getContent())
                    .postImages(post.getPostImages().stream().map(postImageMapper::convertToDto).toList())
                    .lastModified(post.getLastModified())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
        return PostResponse.builder()
                .id(post.getId().asTsid())
                .accountName(post.getAccount().getFullName())
                .avatarImage(post.getAccount().getImage())
                .content(post.getContent())
                .postImages(post.getPostImages().stream().map(postImageMapper::convertToDto).toList())
                .lastModified(post.getLastModified())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
