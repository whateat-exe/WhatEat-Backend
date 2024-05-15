package com.exe.whateat.application.user.response;

import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class UserResponse {

    private Tsid id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String status;
    private String role;
}
