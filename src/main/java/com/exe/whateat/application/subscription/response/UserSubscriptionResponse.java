package com.exe.whateat.application.subscription.response;

import com.exe.whateat.entity.common.ActiveStatus;
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
public class UserSubscriptionResponse {

    private Tsid id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private ActiveStatus status;
}
