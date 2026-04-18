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
@Builder
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
    @Builder.Default
    private CapsuleStatus status = CapsuleStatus.SEALED;

    @Column(name = "ai_reflection", columnDefinition = "TEXT")
    private String aiReflection;

    @Column(name = "background_texture")
    private String backgroundTexture;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CapsuleRecipient> recipients = new ArrayList<>();

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CapsuleFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "capsule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CapsuleDoodle> doodles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
