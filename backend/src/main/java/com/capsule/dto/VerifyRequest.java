package com.capsule.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class VerifyRequest {
    @NotBlank(message = "Email is required for verification")
    @Email(message = "Invalid email format")
    private String email;

    public VerifyRequest() {}

    public VerifyRequest(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
}
