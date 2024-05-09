package com.exe.whateat.application.account_user.getMethod.response;

import com.exe.whateat.application.account_user.dto.UserDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserResponse {

    private List<UserDTO> userDTOS;
    private int pageNumber;
    private int pageSize;
    private int totalElemnts;
}
