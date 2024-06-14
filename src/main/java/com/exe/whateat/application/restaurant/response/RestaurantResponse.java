package com.exe.whateat.application.restaurant.response;

import com.exe.whateat.entity.common.ActiveStatus;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;

@Builder
public record RestaurantResponse(Tsid id, String name, String description, String image, String address,
                                 ActiveStatus status) {

}