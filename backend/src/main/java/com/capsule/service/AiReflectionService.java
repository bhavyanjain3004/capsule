package com.capsule.service;

import com.capsule.model.Capsule;
import com.capsule.repository.CapsuleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiReflectionService {

    private static final Logger log = LoggerFactory.getLogger(AiReflectionService.class);
    private final CapsuleRepository capsuleRepo;
    private final RestTemplate restTemplate = new RestTemplate();

    public AiReflectionService(CapsuleRepository capsuleRepo) {
        this.capsuleRepo = capsuleRepo;
    }

    @Value("${ai.api-key:placeholder}")
    private String apiKey;

    @Value("${ai.enabled:false}")
    private boolean enabled;

    @Async
    public void generateReflectionIfAbsent(Capsule capsule, String content) {
        if (!enabled || capsule.getAiReflection() != null || content == null || content.isBlank()) {
            return;
        }

        log.info("Generating AI reflection for capsule {}", capsule.getId());

        try {
            String url = "https://api.anthropic.com/v1/messages";
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");
            headers.setContentType(MediaType.APPLICATION_JSON);

            String prompt = "You are a poetic observer. Read this memory from a time capsule and write a 2-3 sentence 'reflection' " +
                           "that captures its essence. Be nostalgic, kind, and slightly mysterious. " +
                           "Memory: " + content;

            Map<String, Object> body = Map.of(
                "model", "claude-3-5-sonnet-20240620",
                "max_tokens", 150,
                "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, entity, new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> contentList = (List<Map<String, Object>>) response.getBody().get("content");
                String reflection = (String) contentList.get(0).get("text");
                
                capsule.setAiReflection(reflection.trim());
                capsuleRepo.save(capsule);
                log.info("AI reflection generated successfully for capsule {}", capsule.getId());
            }
        } catch (Exception e) {
            log.error("AI reflection failed for capsule {}: {}", capsule.getId(), e.getMessage());
        }
    }
}
