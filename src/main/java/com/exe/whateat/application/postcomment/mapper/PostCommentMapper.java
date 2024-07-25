package com.exe.whateat.application.postcomment.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.exception.WhatEatErrorCode;
import com.exe.whateat.application.exception.WhatEatException;
import com.exe.whateat.application.postcomment.response.PostCommentResponse;
import com.exe.whateat.entity.account.AccountRole;
import com.exe.whateat.entity.common.WhatEatId;
import com.exe.whateat.entity.post.PostComment;
import com.exe.whateat.entity.restaurant.Restaurant;
import com.exe.whateat.infrastructure.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostCommentMapper implements WhatEatMapper<PostComment, PostCommentResponse> {

    private RestaurantRepository restaurantRepository;

    @Override
    public PostCommentResponse convertToDto(PostComment postComment) {
        if (postComment == null) {
            return null;
        }
        if (postComment.getAccount().getRole() == AccountRole.RESTAURANT){
            WhatEatId whatEatId = postComment.getAccount().getId();
            Restaurant restaurant = restaurantRepository.findByAccountId(whatEatId)
                    .orElseThrow(() -> WhatEatException.builder()
                            .code(WhatEatErrorCode.WES_0001)
                            .reason("restaurant", "Lỗi gửi sai id của restaurant")
                            .build());
            return PostCommentResponse.builder()
                    .id(postComment.getId().asTsid())
                    .accountId(restaurant.getId().asTsid())
                    .fullName(restaurant.getName())
                    .createdAt(postComment.getCreatedAt())
                    .imageUrl(restaurant.getImage())
                    .isModified(postComment.getVersion() > 0)
                    .content(postComment.getContent())
                    .build();
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
