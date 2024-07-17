package com.exe.whateat.application.subscription.response;

import com.exe.whateat.application.common.response.PaginationResponse;
import lombok.Getter;

import java.util.List;

public class RestaurantSubscriptionTrackersResponse extends PaginationResponse<RestaurantSubscriptionTrackerResponse> {

    @Getter
    private final Long totalSubscription;

    public RestaurantSubscriptionTrackersResponse(List<RestaurantSubscriptionTrackerResponse> data, Long total, Long totalSubscription) {
        super(data, total);
        this.totalSubscription = totalSubscription;
    }

}
