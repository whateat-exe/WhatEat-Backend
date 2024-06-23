package com.exe.whateat.application.dish.response;

import com.exe.whateat.entity.common.Money;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class DishResponse {

    private Tsid id;
    private String name;
    private String description;
    private String image;
    private String status;
    private Money price;
    private Double avgReview;
    private Long numOfReview;
    private Tsid restaurantId;
    private Tsid foodId;
}
