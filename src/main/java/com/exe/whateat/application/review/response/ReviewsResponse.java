package com.exe.whateat.application.review.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class ReviewsResponse extends PaginationResponse<ReviewResponse> {
    public ReviewsResponse(List<ReviewResponse> data, Long total) {
        super(data, total);
    }
}
