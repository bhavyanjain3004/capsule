package com.capsule.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "capsule_file")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "capsule_id", nullable = false)
    private Capsule capsule;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "storage_url", nullable = false)
    private String storageUrl;

    @Column(name = "file_size_kb", nullable = false)
    private Integer fileSizeKb;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public String getFileType() { return fileType; }
    public String getStorageUrl() { return storageUrl; }
    public Integer getFileSizeKb() { return fileSizeKb; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setCapsule(Capsule capsule) { this.capsule = capsule; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public void setStorageUrl(String storageUrl) { this.storageUrl = storageUrl; }
    public void setFileSizeKb(Integer fileSizeKb) { this.fileSizeKb = fileSizeKb; }

    public static CapsuleFileBuilder builder() {
        return new CapsuleFileBuilder();
    }

    public static class CapsuleFileBuilder {
        private Capsule capsule;
        private String fileType;
        private String storageUrl;
        private Integer fileSizeKb;

        public CapsuleFileBuilder capsule(Capsule capsule) {
            this.capsule = capsule;
            return this;
        }

        public CapsuleFileBuilder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public CapsuleFileBuilder storageUrl(String storageUrl) {
            this.storageUrl = storageUrl;
            return this;
        }

        public CapsuleFileBuilder fileSizeKb(Integer fileSizeKb) {
            this.fileSizeKb = fileSizeKb;
            return this;
        }

        public CapsuleFile build() {
            CapsuleFile file = new CapsuleFile();
            file.setCapsule(capsule);
            file.setFileType(fileType);
            file.setStorageUrl(storageUrl);
            file.setFileSizeKb(fileSizeKb);
            return file;
        }
    }
}
