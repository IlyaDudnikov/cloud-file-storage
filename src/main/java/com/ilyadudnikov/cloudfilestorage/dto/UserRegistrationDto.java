package com.ilyadudnikov.cloudfilestorage.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistrationDto {
    @NotEmpty(message = "Username can't be empty")
    @Size(min = 2, max = 50, message = "The username must be between 2 and 50 characters long")
    private String username;

    @NotEmpty(message = "Password can't be empty")
    private String password;

    @NotEmpty(message = "Confirm password can't be empty")
    private String confirmPassword;

    @Size(min = 8, max = 100, message = "The email must be between 2 and 50 characters long")
    private String email;
}
