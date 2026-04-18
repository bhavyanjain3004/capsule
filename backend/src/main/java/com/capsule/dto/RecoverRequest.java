package com.capsule.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoverRequest {
    @NotBlank(message = "Email is required for recovery")
    @Email(message = "Invalid email format")
    private String email;

    public String getEmail() { return email; }
}
