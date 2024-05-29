package com.exe.whateat.application.foodtag.response;

import com.exe.whateat.application.food.response.FoodResponse;
import com.exe.whateat.application.tag.response.TagResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FoodTagResponse {

    private Tsid id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FoodResponse food;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TagResponse tag;
}
