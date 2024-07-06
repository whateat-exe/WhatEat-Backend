package com.exe.whateat.application.subscription.response;

import com.exe.whateat.entity.common.ActiveStatus;
import com.exe.whateat.entity.subscription.RestaurantSubscriptionType;
import io.github.x4ala1c.tsid.Tsid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSubscriptionResponse {

    private Tsid id;
    private String name;
    private String description;
    private RestaurantSubscriptionType type;
    private BigDecimal price;
    private Integer duration;
    private ActiveStatus status;
}
