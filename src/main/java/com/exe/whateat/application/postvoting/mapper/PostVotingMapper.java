package com.exe.whateat.application.postvoting.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.postvoting.response.PostVotingResponse;
import com.exe.whateat.entity.post.PostVoting;
import org.springframework.stereotype.Component;

@Component
public class PostVotingMapper implements WhatEatMapper<PostVoting, PostVotingResponse> {
    @Override
    public PostVotingResponse convertToDto(PostVoting postVoting) {
        if (postVoting == null) {
            return null;
        }
        return PostVotingResponse.builder()
                .id(postVoting.getId().asTsid())
                .postId(postVoting.getPost().getId().asTsid())
                .accountId(postVoting.getAccount().getId().asTsid())
                .type(postVoting.getType())
                .build();
    }
}
