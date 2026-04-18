package com.capsule.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    String upload(Long capsuleId, MultipartFile file) throws IOException;
    String generatePresignedUrl(String key);
    void delete(String key);
}
