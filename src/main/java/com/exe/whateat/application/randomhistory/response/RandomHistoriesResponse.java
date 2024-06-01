package com.exe.whateat.application.randomhistory.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public final class RandomHistoriesResponse extends PaginationResponse<RandomHistoryResponse> {

    public RandomHistoriesResponse(List<RandomHistoryResponse> data, Long total) {
        super(data, total);
    }
}
