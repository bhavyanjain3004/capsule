package com.capsule.service;

import com.capsule.model.Capsule;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

/**
 * Interface stubs to resolve dependencies for CapsuleService.
 * Implementation details will be added in future sessions.
 */
interface StorageService {
    String upload(Long capsuleId, MultipartFile file) throws IOException;
    String generatePresignedUrl(String key);
    void delete(String key);
}

interface SqsService {
    void enqueue(Long capsuleId, Instant unlockAt);
}

interface EmailService {
    void sendRecoveryEmail(String email, List<Capsule> capsules);
}

interface AiReflectionService {
    void generateReflectionIfAbsent(Capsule capsule, String content);
}

@Service
class StubStorageService implements StorageService {
    @Override public String upload(Long capsuleId, MultipartFile file) { return "mock-key-" + file.getOriginalFilename(); }
    @Override public String generatePresignedUrl(String key) { return "http://mock-s3.com/" + key; }
    @Override public void delete(String key) {}
}

@Service
class StubSqsService implements SqsService {
    @Override public void enqueue(Long capsuleId, Instant unlockAt) {}
}

@Service
class StubEmailService implements EmailService {
    @Override public void sendRecoveryEmail(String email, List<Capsule> capsules) {}
}

@Service
class StubAiReflectionService implements AiReflectionService {
    @Override public void generateReflectionIfAbsent(Capsule capsule, String content) {}
}
