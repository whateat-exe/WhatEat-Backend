package com.exe.whateat.application.subscription.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class UserSubscriptionTrackersResponse extends PaginationResponse<UserSubscriptionTrackerResponse> {

    public UserSubscriptionTrackersResponse(List<UserSubscriptionTrackerResponse> data, Long total) {
        super(data, total);
    }
}
