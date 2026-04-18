package com.capsule.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

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
    private String canvasJson;

    public CreateCapsuleRequest() {}

    public CreateCapsuleRequest(String creatorEmail, String title, String content, 
                               LocalDateTime unlockAt, List<String> recipients, 
                               String backgroundTexture) {
        this.creatorEmail = creatorEmail;
        this.title = title;
        this.content = content;
        this.unlockAt = unlockAt;
        this.recipients = recipients;
        this.backgroundTexture = backgroundTexture;
    }

    // Manual getters to bypass Lombok issues
    public String getCreatorEmail() { return creatorEmail; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getUnlockAt() { return unlockAt; }
    public List<String> getRecipients() { return recipients; }
    public String getBackgroundTexture() { return backgroundTexture; }
    public String getCanvasJson() { return canvasJson; }
    public void setCanvasJson(String canvasJson) { this.canvasJson = canvasJson; }
}
