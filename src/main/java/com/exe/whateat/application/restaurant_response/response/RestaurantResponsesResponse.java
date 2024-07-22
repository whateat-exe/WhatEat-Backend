package com.exe.whateat.application.restaurant_response.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class RestaurantResponsesResponse extends PaginationResponse<RestaurantResponseResponse> {
    public RestaurantResponsesResponse(List data, Long total) {
        super(data, total);
    }
}
