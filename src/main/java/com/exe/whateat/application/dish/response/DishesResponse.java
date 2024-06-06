package com.exe.whateat.application.dish.response;

import com.exe.whateat.application.common.response.PaginationResponse;
import com.exe.whateat.application.food.response.FoodResponse;

import java.util.List;

public class DishesResponse extends PaginationResponse<DishResponse> {

    public DishesResponse(List<DishResponse> data, Long total) {
        super(data, total);
    }
}
