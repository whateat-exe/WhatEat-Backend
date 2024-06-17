package com.exe.whateat.application.review.response;

import com.exe.whateat.application.dish.response.DishResponse;
import com.exe.whateat.application.user.response.UserResponse;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Builder
@Setter
@Getter
public class ReviewResponse {

    private Tsid id;
    private Integer stars;
    private String feedback;
    private DishResponse dishResponse;
    private UserResponse userResponse;
    private Instant createdAt;
    private Instant lastModified;

}
