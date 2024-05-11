package com.exe.whateat.application.account_user.updateMethod.response;

import com.exe.whateat.application.account_user.dto.UserDTO;
import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
public class UpdateUserResponse {

    private UserDTO userDTO;
}
