package com.capsule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${storage.mode:local}")
    private String mode;

    @Value("${aws.s3.bucket:capsule-files}")
    private String bucket;

    @Value("${capsule.base-url}")
    private String baseUrl;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public StorageService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public String upload(Long capsuleId, MultipartFile file) throws IOException {
        String key = capsuleId + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        if ("r2".equalsIgnoreCase(mode)) {
            s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build(), software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
            return key;
        } else {
            Path path = Paths.get("uploads", key);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            return key;
        }
    }

    public String generatePresignedUrl(String key) {
        if ("r2".equalsIgnoreCase(mode)) {
            return s3Presigner.presignGetObject(GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .getObjectRequest(builder -> builder.bucket(bucket).key(key))
                .build()).url().toString();
        } else {
            return baseUrl + "/api/files/" + key;
        }
    }

    public void delete(String key) {
        if ("r2".equalsIgnoreCase(mode)) {
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(key));
        } else {
            try {
                Files.deleteIfExists(Paths.get("uploads", key));
            } catch (IOException ignored) {}
        }
    }
}
