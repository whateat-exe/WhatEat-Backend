package com.exe.whateat.application.review.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SummaryResponse {

    private Double avgReview;
    private Long numOfReview;
    private Long numOfFiveStar;
    private Long numOfFourStar;
    private Long numOfThreeStar;
    private Long numOfTwoStar;
    private Long numOfOneStar;

}
