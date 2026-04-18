package com.capsule.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "capsule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Capsule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID token;

    @Column(name = "encrypted_secret", nullable = false)
    private String encryptedSecret;

    @Column(name = "encryption_key_hash")
    private String encryptionKeyHash;

    @Column(name = "creator_email", nullable = false)
    private String creatorEmail;

    private String title;

    @Column(name = "encrypted_content", columnDefinition = "TEXT")
    private String encryptedContent;

    @Column(name = "unlock_at", nullable = false)
    private LocalDateTime unlockAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CapsuleStatus status = CapsuleStatus.SEALED;

    @Column(name = "ai_reflection", columnDefinition = "TEXT")
    private String aiReflection;

    @Column(name = "background_texture")
    private String backgroundTexture;

    @Column(name = "encrypted_canvas_json", columnDefinition = "TEXT")
    private String encryptedCanvasJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CapsuleRecipient> recipients = new ArrayList<>();

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CapsuleFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CapsuleDoodle> doodles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (token == null) token = UUID.randomUUID();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Manual Getters and Setters
    public Long getId() { return id; }
    public UUID getToken() { return token; }
    public void setToken(UUID token) { this.token = token; }
    public String getEncryptedSecret() { return encryptedSecret; }
    public void setEncryptedSecret(String encryptedSecret) { this.encryptedSecret = encryptedSecret; }
    public String getEncryptionKeyHash() { return encryptionKeyHash; }
    public void setEncryptionKeyHash(String encryptionKeyHash) { this.encryptionKeyHash = encryptionKeyHash; }
    public String getCreatorEmail() { return creatorEmail; }
    public void setCreatorEmail(String creatorEmail) { this.creatorEmail = creatorEmail; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getEncryptedContent() { return encryptedContent; }
    public void setEncryptedContent(String encryptedContent) { this.encryptedContent = encryptedContent; }
    public LocalDateTime getUnlockAt() { return unlockAt; }
    public void setUnlockAt(LocalDateTime unlockAt) { this.unlockAt = unlockAt; }
    public CapsuleStatus getStatus() { return status; }
    public void setStatus(CapsuleStatus status) { this.status = status; }
    public String getAiReflection() { return aiReflection; }
    public void setAiReflection(String aiReflection) { this.aiReflection = aiReflection; }
    public String getBackgroundTexture() { return backgroundTexture; }
    public void setBackgroundTexture(String backgroundTexture) { this.backgroundTexture = backgroundTexture; }
    public List<CapsuleRecipient> getRecipients() { return recipients; }
    public List<CapsuleFile> getFiles() { return files; }
    public List<CapsuleDoodle> getDoodles() { return doodles; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public String getEncryptedCanvasJson() { return encryptedCanvasJson; }
    public void setEncryptedCanvasJson(String json) { this.encryptedCanvasJson = json; }

    public static CapsuleBuilder builder() {
        return new CapsuleBuilder();
    }

    public static class CapsuleBuilder {
        private UUID token;
        private String creatorEmail;
        private String title;
        private String encryptedSecret;
        private String encryptionKeyHash;
        private String encryptedContent;
        private LocalDateTime unlockAt;
        private String backgroundTexture;
        private String encryptedCanvasJson;
        private CapsuleStatus status;

        public CapsuleBuilder token(UUID token) { this.token = token; return this; }
        public CapsuleBuilder creatorEmail(String creatorEmail) { this.creatorEmail = creatorEmail; return this; }
        public CapsuleBuilder title(String title) { this.title = title; return this; }
        public CapsuleBuilder encryptedSecret(String encryptedSecret) { this.encryptedSecret = encryptedSecret; return this; }
        public CapsuleBuilder encryptionKeyHash(String encryptionKeyHash) { this.encryptionKeyHash = encryptionKeyHash; return this; }
        public CapsuleBuilder encryptedContent(String encryptedContent) { this.encryptedContent = encryptedContent; return this; }
        public CapsuleBuilder unlockAt(LocalDateTime unlockAt) { this.unlockAt = unlockAt; return this; }
        public CapsuleBuilder backgroundTexture(String backgroundTexture) { this.backgroundTexture = backgroundTexture; return this; }
        public CapsuleBuilder encryptedCanvasJson(String json) { this.encryptedCanvasJson = json; return this; }
        public CapsuleBuilder status(CapsuleStatus status) { this.status = status; return this; }

        public Capsule build() {
            Capsule capsule = new Capsule();
            capsule.setToken(token);
            capsule.setCreatorEmail(creatorEmail);
            capsule.setTitle(title);
            capsule.setEncryptedSecret(encryptedSecret);
            capsule.setEncryptionKeyHash(encryptionKeyHash);
            capsule.setEncryptedContent(encryptedContent);
            capsule.setUnlockAt(unlockAt);
            capsule.setBackgroundTexture(backgroundTexture);
            capsule.setEncryptedCanvasJson(encryptedCanvasJson);
            if (status != null) capsule.setStatus(status);
            return capsule;
        }
    }
}
