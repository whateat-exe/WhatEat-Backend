package com.exe.whateat.application.postcomment.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class PostCommentsResponse extends PaginationResponse<PostCommentResponse> {
    public PostCommentsResponse(List<PostCommentResponse> data, Long total) {
        super(data, total);
    }
}
