package com.exe.whateat.application.personalprofile.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public final class PersonalProfilesResponse {

    private List<PersonalProfileResponse> like;
    private List<PersonalProfileResponse> dislike;
    private List<PersonalProfileResponse> allergy;
}
