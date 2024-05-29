package com.exe.whateat.application.foodtag.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public final class FoodTagsResponse extends PaginationResponse<FoodTagResponse> {

    public FoodTagsResponse(List<FoodTagResponse> data, Long total) {
        super(data, total);
    }
}
