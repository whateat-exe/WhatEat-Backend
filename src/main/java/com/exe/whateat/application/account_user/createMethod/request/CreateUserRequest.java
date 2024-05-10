package com.exe.whateat.application.account_user.createMethod.request;

import com.exe.whateat.application.common.WhatEatRegex;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
//    @Email(message = "Invalid Email", regexp = WhatEatRegex.emailPattern)
    private String email;

    @NotNull
//    @Min(value = 8, message = "The length of password has to be larger than 8")
//    @Max(value = 32, message = "The length of password has to be less than 32")
    @Size(min = 8, max = 32, message = "The length of password has to be larger than 8 and less than 32 ")
    private String password;

    @NotBlank
//    @Max(value = 100, message = "The length of full name has to be less than 100")
    @Size(max = 100, message = "The length of full name has to be less than 100")
    private String fullName;

    @NotBlank
//    @Max(value = 20, message = "The length of phone number has to be less than 20")
    @Size(max = 20, message = "The length of phone number has to be less than 20")
    private String phoneNumber;
}
