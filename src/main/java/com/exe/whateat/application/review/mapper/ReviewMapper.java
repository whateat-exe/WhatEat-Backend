package com.exe.whateat.application.review.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.dish.mapper.DishMapper;
import com.exe.whateat.application.review.response.ReviewResponse;
import com.exe.whateat.application.user.mapper.AccountDTOMapper;
import com.exe.whateat.entity.random.Rating;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ReviewMapper implements WhatEatMapper<Rating, ReviewResponse> {

    private DishMapper dishMapper;
    private AccountDTOMapper userMapper;

    @Override
    public ReviewResponse convertToDto(Rating rating) {
        if (rating == null) {
            return null;
        }
        return ReviewResponse.builder()
                .id(rating.getId().asTsid())
                .stars(rating.getStars())
                .feedback(rating.getFeedback())
                .createdAt(rating.getCreatedAt())
                .lastModified(rating.getLastModified())
                .dishResponse(dishMapper.convertToDto(rating.getDish()))
                .userResponse(userMapper.convertToDto(rating.getAccount()))
                .build();
    }

}
