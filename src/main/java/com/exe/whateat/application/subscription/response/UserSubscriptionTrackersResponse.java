package com.exe.whateat.application.subscription.response;

import com.exe.whateat.application.common.response.PaginationResponse;
import lombok.Getter;

import java.util.List;

public class UserSubscriptionTrackersResponse extends PaginationResponse<UserSubscriptionTrackerResponse> {

    @Getter
    private Long totalSubscription;
    public UserSubscriptionTrackersResponse(List<UserSubscriptionTrackerResponse> data, Long total, Long totalSubscription) {
        super(data, total);
        this.totalSubscription = totalSubscription;
    }
}
