package com.exe.whateat.application.food.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public final class FoodsResponse extends PaginationResponse<FoodResponse> {

    public FoodsResponse(List<FoodResponse> data, long total) {
        super(data, total);
    }
}
