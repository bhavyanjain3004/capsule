package com.capsule.dto;

import com.capsule.model.CapsuleStatus;
import java.time.LocalDateTime;

public class PreviewResponse {
    private CapsuleStatus status;
    private LocalDateTime unlocksAt;
    private String title;

    public PreviewResponse() {}

    public PreviewResponse(CapsuleStatus status, LocalDateTime unlocksAt, String title) {
        this.status = status;
        this.unlocksAt = unlocksAt;
        this.title = title;
    }

    public CapsuleStatus getStatus() { return status; }
    public void setStatus(CapsuleStatus status) { this.status = status; }
    public LocalDateTime getUnlocksAt() { return unlocksAt; }
    public void setUnlocksAt(LocalDateTime unlocksAt) { this.unlocksAt = unlocksAt; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
