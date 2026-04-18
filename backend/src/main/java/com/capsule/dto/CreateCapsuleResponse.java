package com.capsule.dto;


import java.util.UUID;

public class CreateCapsuleResponse {
    private UUID token;
    private String capsuleUrl;

    public CreateCapsuleResponse() {}

    public CreateCapsuleResponse(UUID token, String capsuleUrl) {
        this.token = token;
        this.capsuleUrl = capsuleUrl;
    }

    public UUID getToken() { return token; }
    public void setToken(UUID token) { this.token = token; }
    public String getCapsuleUrl() { return capsuleUrl; }
    public void setCapsuleUrl(String capsuleUrl) { this.capsuleUrl = capsuleUrl; }
}
