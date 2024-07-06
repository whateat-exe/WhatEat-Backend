package com.exe.whateat.application.subscription.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class RestaurantSubscriptionTrackersResponse extends PaginationResponse<RestaurantSubscriptionTrackerResponse> {

    public RestaurantSubscriptionTrackersResponse(List<RestaurantSubscriptionTrackerResponse> data, Long total) {
        super(data, total);
    }
}
