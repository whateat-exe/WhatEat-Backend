package com.exe.whateat.application.restaurant_request.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class RequestResponses extends PaginationResponse<RequestResponse> {
    protected RequestResponses(List<RequestResponse> data, Long total) {
        super(data, total);
    }
}
