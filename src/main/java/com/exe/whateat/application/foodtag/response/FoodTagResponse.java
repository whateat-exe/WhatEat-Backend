package com.exe.whateat.application.foodtag.response;

import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.tag.response.TagResponse;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FoodTagResponse {

    private Tsid tsid;
    private TagResponse tagResponse;
    private FoodResponse foodResponse;
}
