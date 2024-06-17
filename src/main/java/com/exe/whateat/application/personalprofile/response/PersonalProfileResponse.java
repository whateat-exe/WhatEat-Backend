package com.exe.whateat.application.personalprofile.response;

import com.exe.whateat.entity.profile.ProfileType;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class PersonalProfileResponse {

    private Tsid id;
    private Tsid tagId;
    private String tagName;
    private ProfileType type;
}
