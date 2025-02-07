package com.exe.whateat.application.restaurant_response.response;

import com.exe.whateat.entity.request.RequestStatus;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class RestaurantResponseResponse {

    private Tsid id;
    private String title;
    private String content;
    private RequestStatus status;
    private Instant createdAt;
    private Tsid restaurantRequestId;
}
