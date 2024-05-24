package com.exe.whateat.application.user.response;

import com.exe.whateat.application.common.response.PaginationResponse;

import java.util.List;

public class UsersResponse extends PaginationResponse<UserResponse> {

    public UsersResponse(List<UserResponse> data, Long total) {
        super(data, total);
    }
}
