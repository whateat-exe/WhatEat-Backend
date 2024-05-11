package com.exe.whateat.application.account_user.dto;
import io.github.x4ala1c.tsid.Tsid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class UserDTO {

    private Tsid id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String status;
    private String role;
}