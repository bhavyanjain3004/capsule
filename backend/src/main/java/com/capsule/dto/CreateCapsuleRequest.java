package com.capsule.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCapsuleRequest {

    @NotBlank(message = "Creator email is required")
    @Email(message = "Invalid email format")
    private String creatorEmail;

    private String title;

    private String content;

    @NotNull(message = "Unlock date is required")
    @Future(message = "Unlock date must be in the future")
    private LocalDateTime unlockAt;

    @NotEmpty(message = "Recipients list cannot be empty")
    private List<@Email(message = "Invalid recipient email format") String> recipients;

    private String backgroundTexture;
}
