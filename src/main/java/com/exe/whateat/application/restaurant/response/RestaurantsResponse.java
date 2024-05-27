package com.exe.whateat.application.restaurant.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public final class RestaurantsResponse extends PaginationResponse<RestaurantResponse> {

    public RestaurantsResponse(List<RestaurantResponse> data, Long total) {
        super(data, total);
    }
}
