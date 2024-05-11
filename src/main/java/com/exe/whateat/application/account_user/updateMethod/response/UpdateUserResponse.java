package com.exe.whateat.application.account_user.updateMethod.response;

import com.exe.whateat.application.account_user.dto.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class UpdateUserResponse {

    private UserDTO userDTO;
}
