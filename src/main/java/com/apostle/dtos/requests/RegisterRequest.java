package com.apostle.dtos.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;


public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "Username must be alphanumeric and between 3 and 20 characters")
    private String username;
    @Email(message = "Email is invalid")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address")
    private String email;
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;


    public @NotBlank(message = "Username is required") @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "Username must be alphanumeric and between 3 and 20 characters") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username is required") @Pattern(regexp = "^[a-zA-Z0-9_-]{3,20}$", message = "Username must be alphanumeric and between 3 and 20 characters") String username) {
        this.username = username;
    }

    public @Email(message = "Email is invalid") @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Email is invalid") @NotBlank @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Please provide a valid email address") String email) {
        this.email = email;
    }

    public @NotBlank(message = "Password is required") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one digit, one lowercase, one uppercase, and one special character") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must be at least 8 characters long and contain at least one digit, one lowercase, one uppercase, and one special character") String password) {
        this.password = password;
    }



}
