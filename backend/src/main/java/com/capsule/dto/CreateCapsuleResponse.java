package com.capsule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCapsuleResponse {
    private UUID token;
    private String capsuleUrl;

    public CreateCapsuleResponse(UUID token, String capsuleUrl) {
        this.token = token;
        this.capsuleUrl = capsuleUrl;
    }
}
