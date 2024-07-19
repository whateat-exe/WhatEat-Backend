package com.exe.whateat.application.restaurant_request.response;

import com.exe.whateat.application.restaurant.response.RestaurantResponse;
import com.exe.whateat.entity.request.RequestType;
import io.github.x4ala1c.tsid.Tsid;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RequestResponse {

    private Tsid tsid;
    private String title;
    private String content;
    private RequestType type;
    private Instant createdAt;
    private RestaurantResponse restaurant;
}
