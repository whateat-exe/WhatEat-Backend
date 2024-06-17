package com.exe.whateat.application.personalprofile.mapper;

import com.exe.whateat.application.common.WhatEatMapper;
import com.exe.whateat.application.personalprofile.response.PersonalProfileResponse;
import com.exe.whateat.entity.profile.PersonalProfile;
import org.springframework.stereotype.Component;

@Component
public final class PersonalProfileMapper implements WhatEatMapper<PersonalProfile, PersonalProfileResponse> {

    @Override
    public PersonalProfileResponse convertToDto(PersonalProfile personalProfile) {
        if (personalProfile == null) {
            return null;
        }
        return PersonalProfileResponse.builder()
                .id(personalProfile.getId().asTsid())
                .tagId(personalProfile.getTag().getId().asTsid())
                .tagName(personalProfile.getTag().getName())
                .type(personalProfile.getType())
                .build();
    }
}
