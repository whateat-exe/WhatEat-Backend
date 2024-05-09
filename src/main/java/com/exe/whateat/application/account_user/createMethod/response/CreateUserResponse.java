package com.exe.whateat.application.account_user.createMethod.response;

import com.exe.whateat.application.account_user.dto.UserDTO;
import lombok.Builder;
import lombok.Setter;


@Setter
@Builder
public class CreateUserResponse {

    private UserDTO userDTO;
}
