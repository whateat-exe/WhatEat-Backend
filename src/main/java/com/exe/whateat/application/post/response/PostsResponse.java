package com.exe.whateat.application.post.response;

import com.exe.whateat.application.common.response.PaginationResponse;
import lombok.NoArgsConstructor;

import java.util.List;

public class PostsResponse extends PaginationResponse<PostResponse> {

    public PostsResponse(List<PostResponse> data, Long total) {
        super(data, total);
    }
}
