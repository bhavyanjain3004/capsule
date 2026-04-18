package com.capsule.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "capsule_recipient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @Column(nullable = false)
    private String email;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setCapsule(Capsule capsule) { this.capsule = capsule; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    public void setNotifiedAt(LocalDateTime notifiedAt) { this.notifiedAt = notifiedAt; }
    public LocalDateTime getNotifiedAt() { return notifiedAt; }
    public LocalDateTime getOpenedAt() { return openedAt; }
    public void setOpenedAt(LocalDateTime openedAt) { this.openedAt = openedAt; }

    public static CapsuleRecipientBuilder builder() {
        return new CapsuleRecipientBuilder();
    }

    public static class CapsuleRecipientBuilder {
        private Capsule capsule;
        private String email;

        public CapsuleRecipientBuilder capsule(Capsule capsule) {
            this.capsule = capsule;
            return this;
        }

        public CapsuleRecipientBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CapsuleRecipient build() {
            CapsuleRecipient recipient = new CapsuleRecipient();
            recipient.setCapsule(capsule);
            recipient.setEmail(email);
            return recipient;
        }
    }
}
