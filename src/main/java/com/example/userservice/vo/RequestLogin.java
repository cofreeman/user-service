package com.example.userservice.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    @NotNull(message = "email cannot be null")
    @Size(min = 2,message = "email not be less than two character")
    @Email
    private String email;
    @NotNull(message = "password cannot be null")
    @Size(min = 2,message = "password not be less than two character")
    private String password;
}
